package com.example.endavapwj.services.AuthenticationService;

import com.example.endavapwj.DTOs.AuthenticationDTO.LoginDTO;
import com.example.endavapwj.DTOs.AuthenticationDTO.LoginResultDTO;
import com.example.endavapwj.DTOs.AuthenticationDTO.RegisterDTO;
import com.example.endavapwj.collection.EmailValidation;
import com.example.endavapwj.collection.User;
import com.example.endavapwj.enums.Role;
import com.example.endavapwj.exceptions.AccountLockedException;
import com.example.endavapwj.exceptions.AlreadyExistsException;
import com.example.endavapwj.exceptions.InvalidFieldException;
import com.example.endavapwj.exceptions.NotPermittedException;
import com.example.endavapwj.repositories.EmailValidationRepository;
import com.example.endavapwj.repositories.UserRepository;
import com.example.endavapwj.util.JwtUtil;
import com.example.endavapwj.util.LoginThrottle;
import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.transactional.SendContact;
import com.mailjet.client.transactional.SendEmailsRequest;
import com.mailjet.client.transactional.TrackOpens;
import com.mailjet.client.transactional.TransactionalEmail;
import com.mailjet.client.transactional.response.SendEmailsResponse;
import jakarta.transaction.Transactional;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
  private final BCryptPasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final EmailValidationRepository emailValidation;
  private final JwtUtil jwtUtil;
  private final LoginThrottle loginThrottle;
  private String mjApiKeyPublic;
  private String mjApiKeyPrivate;
  private String noReplyMail;
  private String websiteDomain;
  ClientOptions options;
  MailjetClient client;

  public AuthenticationServiceImpl(
      UserRepository userRepository,
      EmailValidationRepository emailValidation,
      BCryptPasswordEncoder passwordEncoder,
      JwtUtil jwtUtil,
      LoginThrottle loginThrottle,
      @Value("${mailjet.apikey.public}") String mjApiKeyPublic,
      @Value("${mailjet.apikey.private}") String mjApiKeyPrivate,
      @Value("${mail.confirmation}") String noReplyMail,
      @Value("${security.website.domain}") String websiteDomain
      ) {
    this.userRepository = userRepository;
    this.emailValidation = emailValidation;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
    this.loginThrottle = loginThrottle;
    this.noReplyMail = noReplyMail;
    this.websiteDomain = websiteDomain;
    this.mjApiKeyPublic = mjApiKeyPublic;
    this.mjApiKeyPrivate = mjApiKeyPrivate;
    this.options = ClientOptions.builder()
            .apiKey(mjApiKeyPublic)
            .apiSecretKey(mjApiKeyPrivate)
            .build();
    this.client = new MailjetClient(options);
  }

  @Override
  @Transactional
  public CompletableFuture<Map<String, String>> registerUser(RegisterDTO registerDTO) throws MailjetException {
    if (userRepository.existsByUsernameOrEmailIgnoreCase(
        registerDTO.getUsername(), registerDTO.getEmail()))
      throw new AlreadyExistsException("Username or email already exists.");

    User user = new User();
    user.setUsername(registerDTO.getUsername());
    user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
    user.setEmail(registerDTO.getEmail());
    user.setRole(Role.User);
    EmailValidation token = new EmailValidation();
    token.setUser(user);
    token.setValidationHash(RandomStringUtils.secure().nextAlphanumeric(24));
    token.setCreatedAt(new Date());
    if(sendEmail(registerDTO.getEmail(),registerDTO.getUsername(),token.getValidationHash())){
      userRepository.save(user);
      emailValidation.save(token);
    }
    else{
      throw new MailjetException("Email failed to send.");
    }
    return CompletableFuture.completedFuture(
        Map.of("message", "Register successful. Check your email to validate the address."));
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
    User u =
        userRepository
            .findByUsernameIgnoreCase(loginDTO.getUsername())
            .orElseThrow(() -> new InvalidFieldException("Invalid account details."));

    if (loginThrottle.isLocked(u.getId())) {
      long seconds = loginThrottle.getLockRemainingSeconds(u.getId());
      throw new AccountLockedException("Account locked. Try again in " + seconds + " seconds.");
    }

    if (!passwordEncoder.matches(loginDTO.getPassword(), u.getPassword())) {
      loginThrottle.registerFailure(u.getId());
      throw new InvalidFieldException("Invalid account details.");
    }

    loginThrottle.reset(u.getId());
    LoginResultDTO resultDTO =
        LoginResultDTO.builder()
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

  @Override
  public CompletableFuture<Map<String, String>> loggedIn() {
    User u =
        userRepository
            .findByUsernameIgnoreCase(
                SecurityContextHolder.getContext().getAuthentication().getName())
            .orElseThrow(() -> new NotPermittedException("Not logged in."));
    return CompletableFuture.completedFuture(Map.of("username", u.getUsername()));
  }


  private boolean sendEmail(String to, String fullName, String emailHashKey) throws MailjetException {
    if (noReplyMail == null || websiteDomain == null) {
      throw new MailjetException("Email configuration is missing values!");
    }
    TransactionalEmail message1 = TransactionalEmail
            .builder()
            .from(new SendContact(noReplyMail, "Radush Support"))
            .to(new SendContact(to, fullName))
            .htmlPart("<h1>Please confirm your email</h1>" +
                    "<a href=\"" + websiteDomain + "/api/v2/auth/validate/" + emailHashKey + "\">Click here!</a>")
            .subject("radush.ro email confirmation")
            .build();
    SendEmailsRequest request = SendEmailsRequest.builder().message(message1).build();
    SendEmailsResponse response = request.sendWith(client);
    return true;
  }
}
