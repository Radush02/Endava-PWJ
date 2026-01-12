package com.example.endavapwj.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.endavapwj.DTOs.UserDTO.OtherUserDTO;
import com.example.endavapwj.DTOs.UserDTO.UpdateUserDTO;
import com.example.endavapwj.DTOs.UserDTO.UserDTO;
import com.example.endavapwj.enums.Role;
import com.example.endavapwj.exceptions.InvalidFieldException;
import com.example.endavapwj.repositories.UserRepository;
import com.example.endavapwj.services.AuthenticationService.AuthenticationService;
import com.example.endavapwj.services.UserService.UserService;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest extends BaseControllerTest {

  @MockitoBean private AuthenticationService authenticationService;
  @MockitoBean private UserRepository userRepository;
  @MockitoBean private UserService userService;

  @Test
  void me_ShouldReturnUser() throws Exception {
    UserDTO userDTO =
        UserDTO.builder()
            .id(1L)
            .username("radush")
            .email("radush@radush.ro")
            .role(Role.User)
            .fullName("Radush")
            .image("https://radush.ro/assets/images/radush.png")
            .build();

    when(userService.me()).thenReturn(CompletableFuture.completedFuture(userDTO));

    MvcResult mvcResult =
        mvc.perform(get("/api/v2/user/me")).andExpect(request().asyncStarted()).andReturn();

    mvc.perform(asyncDispatch(mvcResult))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.username").value("radush"))
        .andExpect(jsonPath("$.email").value("radush@radush.ro"))
        .andExpect(jsonPath("$.role").value(Role.User.name()))
        .andExpect(jsonPath("$.fullName").value("Radush"))
        .andExpect(jsonPath("$.image").value("https://radush.ro/assets/images/radush.png"));
  }

  @Test
  void update_ShouldReturnAccepted() throws Exception {
    UpdateUserDTO updateDTO =
        UpdateUserDTO.builder().email("new@radush.ro").fullName("Radu C.").build();

    when(userService.update(any(UpdateUserDTO.class)))
        .thenReturn(
            CompletableFuture.completedFuture(Map.of("message", "Profile updated successfully")));

    MvcResult mvcResult =
        mvc.perform(
                put("/api/v2/user/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateDTO)))
            .andExpect(request().asyncStarted())
            .andReturn();

    mvc.perform(asyncDispatch(mvcResult))
        .andExpect(status().isAccepted())
        .andExpect(jsonPath("$.message").value("Profile updated successfully"));
  }

  @Test
  void user_shouldReturnOtherUserDTO() throws Exception {
    String username = "radush";
    OtherUserDTO otherUserDTO =
        OtherUserDTO.builder()
            .username(username)
            .role(Role.User)
            .image("https://radush.ro/assets/images/radush.png")
            .build();

    when(userService.info(otherUserDTO.getUsername()))
        .thenReturn(CompletableFuture.completedFuture(otherUserDTO));

    MvcResult mvcResult =
        mvc.perform(get("/api/v2/user/user/{username}", username))
            .andExpect(request().asyncStarted())
            .andReturn();

    mvc.perform(asyncDispatch(mvcResult))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value(username))
        .andExpect(jsonPath("$.role").value(Role.User.name()))
        .andExpect(jsonPath("$.image").value("https://radush.ro/assets/images/radush.png"));
  }

  @Test
  void user_ShouldReturnBadRequest_WhenUserNotFound() throws Exception {
    when(userService.me())
        .thenReturn(CompletableFuture.failedFuture(new InvalidFieldException("User not found.")));

    MvcResult mvcResult =
        mvc.perform(get("/api/v2/user/me")).andExpect(request().asyncStarted()).andReturn();

    mvc.perform(asyncDispatch(mvcResult))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("User not found."))
        .andExpect(jsonPath("$.timestamp").exists());
  }
}
