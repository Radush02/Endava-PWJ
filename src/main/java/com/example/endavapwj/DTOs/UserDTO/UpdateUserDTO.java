package com.example.endavapwj.DTOs.UserDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
public class UpdateUserDTO {
  String email;
  String username;
  String fullName;
  MultipartFile image;
}
