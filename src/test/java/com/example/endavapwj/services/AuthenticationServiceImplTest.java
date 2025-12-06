package com.example.endavapwj.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import com.example.endavapwj.services.AuthenticationService.AuthenticationServiceImpl;
import com.example.endavapwj.util.JwtUtil;
import com.example.endavapwj.util.LoginThrottle;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

  @Mock private UserRepository userRepository;
  @Mock private EmailValidationRepository emailValidationRepository;
  @Mock private BCryptPasswordEncoder passwordEncoder;
  @Mock private JwtUtil jwtUtil;
  @Mock private LoginThrottle loginThrottle;

  @InjectMocks private AuthenticationServiceImpl service;

  private RegisterDTO validDto;

  @BeforeEach
  void setUp() {
    validDto =
        RegisterDTO.builder()
            .username("radush")
            .password("Password1!")
            .email("radush@radush.ro")
            .build();
  }

  @Test
  void registerUser_whenUsernameOrEmailExists_throwsAlreadyExistsException() {
    when(userRepository.existsByUsernameOrEmailIgnoreCase("radush", "radush@radush.ro"))
        .thenReturn(true);

    assertThrows(AlreadyExistsException.class, () -> service.registerUser(validDto));

    verify(userRepository, never()).save(any(User.class));
    verify(emailValidationRepository, never()).save(any(EmailValidation.class));
  }

  @Test
  void registerUser_whenValid_savesUser_createsToken_andReturnsMessageAndToken() {
    when(userRepository.existsByUsernameOrEmailIgnoreCase("radush", "radush@radush.ro"))
        .thenReturn(false);
    when(passwordEncoder.encode("Password1!")).thenReturn("ENCODED");
    when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    ArgumentCaptor<EmailValidation> tokenCaptor = ArgumentCaptor.forClass(EmailValidation.class);

    CompletableFuture<Map<String, String>> future = service.registerUser(validDto);
    Map<String, String> result = future.join();

    assertEquals("Register successful", result.get("message"));
    assertNotNull(result.get("emailToken"));
    assertEquals(24, result.get("emailToken").length());

    verify(userRepository).save(userCaptor.capture());
    verify(emailValidationRepository).save(tokenCaptor.capture());

    User savedUser = userCaptor.getValue();
    assertEquals("radush", savedUser.getUsername());
    assertEquals("radush@radush.ro", savedUser.getEmail());
    assertEquals(Role.User, savedUser.getRole());
    assertEquals("ENCODED", savedUser.getPassword());

    EmailValidation savedToken = tokenCaptor.getValue();
    assertNotNull(savedToken.getUser());
    assertNotNull(savedToken.getCreatedAt());
    assertNotNull(savedToken.getValidationHash());
    assertEquals(24, savedToken.getValidationHash().length());
    assertEquals(savedToken.getValidationHash(), result.get("emailToken"));
  }

  @Test
  void validateEmail_whenValid_deletesEmailToken_andReturnsMessage() {
    when(userRepository.existsByUsernameOrEmailIgnoreCase("radush", "radush@radush.ro"))
        .thenReturn(false);
    when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
    when(passwordEncoder.encode("Password1!")).thenReturn("ENCODED");

    Map<String, String> result = service.registerUser(validDto).join();

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    ArgumentCaptor<EmailValidation> tokenCaptor = ArgumentCaptor.forClass(EmailValidation.class);

    verify(userRepository).save(userCaptor.capture());
    verify(emailValidationRepository).save(tokenCaptor.capture());

    EmailValidation savedToken = tokenCaptor.getValue();
    String hash = savedToken.getValidationHash();
    assertEquals(24, hash.length());
    assertEquals(hash, result.get("emailToken"));
    when(emailValidationRepository.findByValidationHash(hash)).thenReturn(Optional.of(savedToken));
    Map<String, String> emailResult = service.validateEmail(hash).join();
    assertEquals("Email validated successfully.", emailResult.get("message"));

    verify(emailValidationRepository).delete(savedToken);
    verify(userRepository, atLeastOnce()).save(any(User.class));
  }

  @Test
  void login_whenUserDoesNotExist_throwsInvalidFieldException() {
    when(userRepository.findByUsernameIgnoreCase("missing")).thenReturn(Optional.empty());
    LoginDTO dto = new LoginDTO("missing", "X");
    assertThrows(InvalidFieldException.class, () -> service.login(dto).join());
    verify(userRepository).findByUsernameIgnoreCase("missing");
    verifyNoInteractions(jwtUtil);
    verify(loginThrottle, never()).reset(anyLong());
  }

  @Test
  void login_whenAccountLocked_throwsAccountLockedException() {
    User u =
        User.builder()
            .id(1L)
            .username("radush")
            .password("ENCODED")
            .email("r@r.ro")
            .role(Role.User)
            .build();
    when(userRepository.findByUsernameIgnoreCase("radush")).thenReturn(Optional.ofNullable(u));
    when(loginThrottle.isLocked(1L)).thenReturn(true);
    when(loginThrottle.getLockRemainingSeconds(1L)).thenReturn(42L);

    LoginDTO dto = new LoginDTO("radush", "Password1!");
    AccountLockedException ex =
        assertThrows(AccountLockedException.class, () -> service.login(dto).join());
    assertTrue(ex.getMessage().contains("42"));
    verify(loginThrottle, never()).registerFailure(anyLong());
    verify(loginThrottle, never()).reset(anyLong());
  }

  @Test
  void login_whenPasswordInvalid_registersFailure_andThrowsInvalidFieldException() {
    User u =
        User.builder()
            .id(2L)
            .username("radush")
            .password("ENCODED")
            .email("r2@r.ro")
            .role(Role.User)
            .build();
    when(userRepository.findByUsernameIgnoreCase("radush")).thenReturn(Optional.ofNullable(u));
    when(loginThrottle.isLocked(2L)).thenReturn(false);
    when(passwordEncoder.matches("BadPass!", "ENCODED")).thenReturn(false);

    LoginDTO dto = new LoginDTO("radush", "BadPass!");
    assertThrows(InvalidFieldException.class, () -> service.login(dto).join());

    verify(loginThrottle).registerFailure(2L);
    verify(loginThrottle, never()).reset(anyLong());
    verify(jwtUtil, never()).generateToken(anyString());
  }

  @Test
  void login_whenPasswordValid_returnsTokens_andResetsThrottle() {
    User u =
        User.builder()
            .id(3L)
            .username("radush")
            .password("ENCODED")
            .email("r3@r.ro")
            .role(Role.User)
            .build();
    when(userRepository.findByUsernameIgnoreCase("radush")).thenReturn(Optional.ofNullable(u));
    when(loginThrottle.isLocked(3L)).thenReturn(false);
    when(passwordEncoder.matches("Password1!", "ENCODED")).thenReturn(true);
    when(jwtUtil.generateToken("radush")).thenReturn("ACCESS_TOKEN");
    when(jwtUtil.generateRefreshToken("radush")).thenReturn("REFRESH_TOKEN");

    LoginDTO dto = new LoginDTO("radush", "Password1!");
    LoginResultDTO result = service.login(dto).join();

    assertEquals("ACCESS_TOKEN", result.getAccessToken());
    assertEquals("REFRESH_TOKEN", result.getRefreshToken());

    verify(loginThrottle).reset(3L);
    verify(loginThrottle, never()).registerFailure(anyLong());
    verify(jwtUtil).generateToken("radush");
    verify(jwtUtil).generateRefreshToken("radush");
  }
}
