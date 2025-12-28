package com.example.endavapwj.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.endavapwj.DTOs.AuthenticationDTO.RegisterDTO;
import com.example.endavapwj.exceptions.InvalidFieldException;
import com.example.endavapwj.repositories.*;
import com.example.endavapwj.services.AuthenticationService.AuthenticationService;
import com.example.endavapwj.util.JwtUtil;
import com.example.endavapwj.util.LoginThrottle;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManagerFactory;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerTest {

  @Autowired private MockMvc mvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean(name = "entityManagerFactory")
  private EntityManagerFactory entityManagerFactory;

  @MockitoBean private UserRepository userRepository;
  @MockitoBean private SubmissionRepository submissionRepository;
  @MockitoBean private ProblemRepository problemRepository;
  @MockitoBean private TestCaseRepository testCaseRepository;
  @MockitoBean private EmailValidationRepository emailValidationRepository;
  @MockitoBean private BCryptPasswordEncoder passwordEncoder;
  @MockitoBean private JwtUtil jwtUtil;
  @MockitoBean LoginThrottle loginThrottle;

  @MockitoBean private AuthenticationService authenticationService;

  @Test
  void whenRegisterWithCorrectInfo_thenCreateUser() throws Exception {
    RegisterDTO validDto =
        RegisterDTO.builder()
            .username("radush")
            .password("Password1!")
            .email("radush@radush.ro")
            .build();

    String reqBody = objectMapper.writeValueAsString(validDto);

    Map<String, String> serviceResponse =
        Map.of(
            "message", "Register successful",
            "emailToken", "dummyToken");

    Mockito.when(authenticationService.registerUser(Mockito.any(RegisterDTO.class)))
        .thenReturn(CompletableFuture.completedFuture(serviceResponse));

    MvcResult mvcResult =
        mvc.perform(
                post("/api/v2/auth/register")
                    .contentType("application/json")
                    .characterEncoding(StandardCharsets.UTF_8)
                    .content(reqBody))
            .andExpect(request().asyncStarted())
            .andReturn();

    mvc.perform(asyncDispatch(mvcResult))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.message").value("Register successful"))
        .andExpect(jsonPath("$.emailToken").value("dummyToken"));

    Mockito.verify(authenticationService, Mockito.times(1))
        .registerUser(Mockito.any(RegisterDTO.class));
  }

  @Test
  void whenRegisterWithNotStrongPassword_thenThrowBadRequest() throws Exception {
    RegisterDTO validDto =
        RegisterDTO.builder()
            .username("radush")
            .password("Password1")
            .email("radush@radush.ro")
            .build();

    String reqBody = objectMapper.writeValueAsString(validDto);

    mvc.perform(
            post("/api/v2/auth/register")
                .contentType("application/json")
                .characterEncoding(StandardCharsets.UTF_8)
                .content(reqBody))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(
            jsonPath("$.error[0]")
                .value(
                    "password: Password must include at least one lowercase, one uppercase, one digit and one special character."))
        .andReturn();

    Mockito.verify(authenticationService, Mockito.times(0))
        .registerUser(Mockito.any(RegisterDTO.class));
  }

  @Test
  void whenRegisterWithInvalidEmail_thenThrowBadRequest() throws Exception {
    RegisterDTO validDto =
        RegisterDTO.builder()
            .username("radush")
            .password("Password1!")
            .email("invalideemail")
            .build();

    String reqBody = objectMapper.writeValueAsString(validDto);

    mvc.perform(
            post("/api/v2/auth/register")
                .contentType("application/json")
                .characterEncoding(StandardCharsets.UTF_8)
                .content(reqBody))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(jsonPath("$.error[0]").value("email: Email is invalid"))
        .andReturn();

    Mockito.verify(authenticationService, Mockito.times(0))
        .registerUser(Mockito.any(RegisterDTO.class));
  }

  @Test
  void whenConfirmEmail_thenReturnValidationMessage() throws Exception {

    RegisterDTO validDto =
        RegisterDTO.builder()
            .username("radush")
            .password("Password1!")
            .email("radush@radush.ro")
            .build();

    String reqBody = objectMapper.writeValueAsString(validDto);
    Map<String, String> serviceResponse =
        Map.of(
            "message", "Register successful",
            "emailToken", "dummyToken");

    Mockito.when(authenticationService.registerUser(Mockito.any(RegisterDTO.class)))
        .thenReturn(CompletableFuture.completedFuture(serviceResponse));

    Mockito.when(authenticationService.validateEmail("dummyToken"))
        .thenReturn(
            CompletableFuture.completedFuture(Map.of("message", "Email validated successfully.")));

    MvcResult mvcRegisterResult =
        mvc.perform(
                post("/api/v2/auth/register")
                    .contentType("application/json")
                    .characterEncoding(StandardCharsets.UTF_8)
                    .content(reqBody))
            .andExpect(request().asyncStarted())
            .andReturn();

    mvc.perform(asyncDispatch(mvcRegisterResult))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.message").value("Register successful"))
        .andReturn();

    MvcResult mvcConfirmResult =
        mvc.perform(
                post("/api/v2/auth/validate")
                    .param("emailHashKey", "dummyToken")
                    .contentType("application/json")
                    .characterEncoding(StandardCharsets.UTF_8))
            .andExpect(request().asyncStarted())
            .andReturn();

    mvc.perform(asyncDispatch(mvcConfirmResult))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.message").value("Email validated successfully."));

    Mockito.verify(authenticationService, Mockito.times(1))
        .registerUser(Mockito.any(RegisterDTO.class));
  }

  @Test
  void whenConfirmEmail_withWrongHashKey_thenReturnFailedMessage() throws Exception {
    RegisterDTO validDto =
        RegisterDTO.builder()
            .username("radush")
            .password("Password1!")
            .email("radush@radush.ro")
            .build();

    String reqBody = objectMapper.writeValueAsString(validDto);
    Map<String, String> serviceResponse =
        Map.of(
            "message", "Register successful",
            "emailToken", "dummyToken");

    Mockito.when(authenticationService.validateEmail("incorrectToken"))
        .thenThrow(new InvalidFieldException("error: Invalid email validation token."));
    Mockito.when(authenticationService.registerUser(Mockito.any(RegisterDTO.class)))
        .thenReturn(CompletableFuture.completedFuture(serviceResponse));

    MvcResult mvcRegisterResult =
        mvc.perform(
                post("/api/v2/auth/register")
                    .contentType("application/json")
                    .characterEncoding(StandardCharsets.UTF_8)
                    .content(reqBody))
            .andExpect(request().asyncStarted())
            .andReturn();

    mvc.perform(asyncDispatch(mvcRegisterResult))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.message").value("Register successful"))
        .andReturn();

    mvc.perform(
            post("/api/v2/auth/validate")
                .param("emailHashKey", "incorrectToken")
                .contentType("application/json")
                .characterEncoding(StandardCharsets.UTF_8))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(jsonPath("$.error").value("error: Invalid email validation token."));

    Mockito.verify(authenticationService, Mockito.times(1))
        .registerUser(Mockito.any(RegisterDTO.class));
  }

  void whenLogin_withCorrectPassword_thenReturnToken() throws Exception {}
}
