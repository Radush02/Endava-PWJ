package com.example.endavapwj.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginDTO {
  @NotBlank
  @Pattern(
      regexp = "^[a-zA-Z0-9._-]{3,20}$",
      message =
          "Username contains invalid characters or is of invalid length (more than 3 and less than 20 characters).")
  private String username;

  @NotBlank private String password;
}
