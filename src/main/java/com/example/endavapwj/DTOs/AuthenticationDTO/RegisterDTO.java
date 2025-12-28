package com.example.endavapwj.DTOs.AuthenticationDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegisterDTO {
  @NotBlank
  @Pattern(
      regexp = "^[a-zA-Z0-9._-]{3,20}$",
      message =
          "Username contains invalid characters or is of invalid length (more than 3 and less than 20 characters).")
  public String username;

  @NotBlank
  @Pattern(
      regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
      message =
          "Password must include at least one lowercase, one uppercase, one digit and one special character.")
  public String password;

  @NotBlank
  @Pattern(regexp = ".+@.+\\..+", message = "Email is invalid")
  public String email;
}
