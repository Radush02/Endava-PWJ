package com.example.endavapwj.DTOs;


import com.example.endavapwj.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginResultDTO {
    Long id;
    String username;
    String email;
    String fullName;
    Role role;
    String accessToken;
    String refreshToken;
}
