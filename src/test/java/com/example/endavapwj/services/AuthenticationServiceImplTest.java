package com.example.endavapwj.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.endavapwj.DTOs.AuthenticationDTO.LoginDTO;
import com.example.endavapwj.DTOs.AuthenticationDTO.LoginResultDTO;
import com.example.endavapwj.DTOs.AuthenticationDTO.RegisterDTO;
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

import com.mailjet.client.errors.MailjetException;
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
