package com.example.endavapwj.services.UserService;

import com.example.endavapwj.DTOs.UserDTO.UpdateUserDTO;
import com.example.endavapwj.DTOs.UserDTO.UserDTO;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface UserService {

  public CompletableFuture<UserDTO> me();

  CompletableFuture<Map<String, String>> update(UpdateUserDTO updateUserDTO);
}
