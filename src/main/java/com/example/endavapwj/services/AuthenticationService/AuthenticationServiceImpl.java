package com.example.endavapwj.services.AuthenticationService;

import com.example.endavapwj.DTOs.LoginDTO;
import com.example.endavapwj.DTOs.LoginResultDTO;
import com.example.endavapwj.DTOs.RegisterDTO;
import com.example.endavapwj.collection.EmailValidation;
import com.example.endavapwj.collection.User;
import com.example.endavapwj.enums.Role;
import com.example.endavapwj.exceptions.AccountLockedException;
import com.example.endavapwj.exceptions.AlreadyExistsException;
import com.example.endavapwj.exceptions.InvalidFieldException;
import com.example.endavapwj.repositories.EmailValidationRepository;
import com.example.endavapwj.repositories.UserRepository;
import com.example.endavapwj.util.JwtUtil;
import com.example.endavapwj.util.LoginThrottle;
import jakarta.transaction.Transactional;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
  private final BCryptPasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final EmailValidationRepository emailValidation;
  private final JwtUtil jwtUtil;
  private final LoginThrottle loginThrottle;

  public AuthenticationServiceImpl(
      UserRepository userRepository,
      EmailValidationRepository emailValidation,
      BCryptPasswordEncoder passwordEncoder,
      JwtUtil jwtUtil,
      LoginThrottle loginThrottle) {
    this.userRepository = userRepository;
    this.emailValidation = emailValidation;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
    this.loginThrottle = loginThrottle;
  }

  @Override
  @Transactional
  public CompletableFuture<Map<String, String>> registerUser(RegisterDTO registerDTO) {
    if (userRepository.existsByUsernameOrEmailIgnoreCase(
        registerDTO.getUsername(), registerDTO.getEmail()))
      throw new AlreadyExistsException("Username or email already exists.");

    User user = new User();
    user.setUsername(registerDTO.getUsername());
    user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
    user.setEmail(registerDTO.getEmail());
    user.setRole(Role.User);
    userRepository.save(user);

    EmailValidation token = new EmailValidation();
    token.setUser(user);
    token.setValidationHash(RandomStringUtils.secure().nextAlphanumeric(24));
    token.setCreatedAt(new Date());
    emailValidation.save(token);

    return CompletableFuture.completedFuture(
        Map.of("message", "Register successful", "emailToken", token.getValidationHash()));
  }

  @Override
  @Transactional
  public CompletableFuture<Map<String, String>> validateEmail(String hash) {
    EmailValidation token =
        emailValidation
            .findByValidationHash(hash)
            .orElseThrow(() -> new InvalidFieldException("Invalid email validation token."));
    User user = token.getUser();
    user.setEmailVerifiedAt(new Date());
    userRepository.save(user);
    emailValidation.delete(token);
    return CompletableFuture.completedFuture(Map.of("message", "Email validated successfully."));
  }

  @Transactional
  @Override
  public CompletableFuture<LoginResultDTO> login(LoginDTO loginDTO) {
    User u = userRepository.findByUsernameIgnoreCase(loginDTO.getUsername()).orElseThrow(()->new InvalidFieldException("Invalid account details."));
    if (u == null) {
      throw new InvalidFieldException("Invalid account details.");
    }

    if (loginThrottle.isLocked(u.getId())) {
      long seconds = loginThrottle.getLockRemainingSeconds(u.getId());
      throw new AccountLockedException("Account locked. Try again in " + seconds + " seconds.");
    }

    if (!passwordEncoder.matches(loginDTO.getPassword(), u.getPassword())) {
      loginThrottle.registerFailure(u.getId());
      throw new InvalidFieldException("Invalid account details.");
    }

    loginThrottle.reset(u.getId());
    LoginResultDTO resultDTO = LoginResultDTO.builder()
            .id(u.getId())
            .username(u.getUsername())
            .email(u.getEmail())
            .role(u.getRole())
            .fullName(u.getFullName())
            .accessToken(jwtUtil.generateToken(loginDTO.getUsername()))
            .refreshToken(jwtUtil.generateRefreshToken(loginDTO.getUsername()))
            .build();


    return CompletableFuture.completedFuture(resultDTO);
  }
}
