package com.example.endavapwj.DTOs.UserDTO;

import com.example.endavapwj.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
