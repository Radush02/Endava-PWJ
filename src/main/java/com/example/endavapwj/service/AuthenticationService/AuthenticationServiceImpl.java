package com.example.endavapwj.service.AuthenticationService;

import com.example.endavapwj.DTOs.LoginDTO;
import com.example.endavapwj.DTOs.RegisterDTO;
import com.example.endavapwj.collection.EmailValidation;
import com.example.endavapwj.collection.User;
import com.example.endavapwj.exceptions.AlreadyExistsException;
import com.example.endavapwj.exceptions.InvalidFieldException;
import com.example.endavapwj.repositories.EmailValidationRepository;
import com.example.endavapwj.enums.Role;
import com.example.endavapwj.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final EmailValidationRepository emailValidation;

    public AuthenticationServiceImpl(UserRepository userRepository,
                                     EmailValidationRepository emailValidation,
                                     BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.emailValidation = emailValidation;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public CompletableFuture<Map<String, String>> registerUser(RegisterDTO registerDTO) {
        if (userRepository.existsByUsernameOrEmailIgnoreCase(registerDTO.getUsername(), registerDTO.getEmail()))
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
        emailValidation.save(token);

        return CompletableFuture.completedFuture(
                Map.of("message", "Register successful", "emailToken", token.getValidationHash())
        );
    }


    @Override
    @Transactional
    public CompletableFuture<Map<String, String>> validateEmail(String hash) {
        EmailValidation token = emailValidation.findByValidationHash(hash)
                .orElseThrow(() -> new InvalidFieldException("Invalid email validation token."));
        User user = token.getUser();
        user.setEmailVerifiedAt(new Date());
        userRepository.save(user);
        emailValidation.delete(token);
        return CompletableFuture.completedFuture(
                Map.of("message", "Email validated successfully.")
        );
    }

    @Transactional
    @Override
    public CompletableFuture<Map<String,String>> login(LoginDTO loginDTO){
        User u = userRepository.findByUsernameIgnoreCase(loginDTO.getUsername());
        if(u == null)
            throw new InvalidFieldException("Invalid account details.");
        if(!passwordEncoder.matches(loginDTO.getPassword(), u.getPassword())){
            //implement account lock-out, todo
            throw new InvalidFieldException("Invalid account details.");
        }
        return CompletableFuture.completedFuture(Map.of("message", "Login successful.","access","jwt","refresh","refresh"));
    }

}
