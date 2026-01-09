package com.example.endavapwj.DTOs.UserDTO;

import com.example.endavapwj.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class OtherUserDTO {
  String username;
  Role role;
  String image;
}
