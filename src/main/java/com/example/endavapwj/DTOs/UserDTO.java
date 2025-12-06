package com.example.endavapwj.DTOs;

import com.example.endavapwj.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
public class UserDTO {
    Long id;
    String email;
    String username;
    String fullName;
    Role role;
    String image;
}
