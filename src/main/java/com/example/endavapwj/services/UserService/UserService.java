package com.example.endavapwj.services.UserService;

import com.example.endavapwj.DTOs.UserDTO.OtherUserDTO;
import com.example.endavapwj.DTOs.UserDTO.UpdateUserDTO;
import com.example.endavapwj.DTOs.UserDTO.UserDTO;
import org.reactivestreams.Publisher;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface UserService {

  public CompletableFuture<UserDTO> me();

  CompletableFuture<OtherUserDTO> info(String otherUsername);

  CompletableFuture<Map<String, String>> update(UpdateUserDTO updateUserDTO);

  void promoteToAdmin(String email);
}
