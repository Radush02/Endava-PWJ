package com.example.endavapwj.controllers;

import com.example.endavapwj.DTOs.UserDTO.UpdateUserDTO;
import com.example.endavapwj.DTOs.UserDTO.UserDTO;
import com.example.endavapwj.services.UserService.UserService;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/user")
@AllArgsConstructor
public class UserController {
  private final UserService userService;

  @GetMapping("/me")
  public CompletableFuture<ResponseEntity<UserDTO>> me() {
    return userService.me().thenApply(ResponseEntity::ok);
  }

  @PutMapping("/update")
  public CompletableFuture<ResponseEntity<Map<String, String>>> update(
      @RequestBody UpdateUserDTO updateUserDTO) {
    return userService
        .update(updateUserDTO)
        .thenApply((body) -> ResponseEntity.status(HttpStatus.ACCEPTED).body(body));
  }
}
