package com.example.endavapwj.services.UserService;

import com.example.endavapwj.DTOs.UserDTO.OtherUserDTO;
import com.example.endavapwj.DTOs.UserDTO.UpdateUserDTO;
import com.example.endavapwj.DTOs.UserDTO.UserDTO;
import com.example.endavapwj.DTOs.UserDTO.UserTopDTO;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface UserService {

  CompletableFuture<UserDTO> me();

  CompletableFuture<OtherUserDTO> info(String otherUsername);

  CompletableFuture<Map<String, String>> update(UpdateUserDTO updateUserDTO);

  void promoteToAdmin(String email);

  CompletableFuture<List<UserTopDTO>> getLeaderboard();
}
