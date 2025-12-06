package com.example.endavapwj.services.UserService;

import com.example.endavapwj.DTOs.UserDTO;

import java.util.concurrent.CompletableFuture;

public interface UserService {

    public CompletableFuture<UserDTO> me();
}
