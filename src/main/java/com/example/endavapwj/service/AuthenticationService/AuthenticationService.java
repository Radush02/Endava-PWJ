package com.example.endavapwj.service.AuthenticationService;

import com.example.endavapwj.DTOs.RegisterDTO;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface AuthenticationService {

    CompletableFuture<Map<String, String>> registerUser(RegisterDTO registerDTO);
    CompletableFuture<Map<String,String>> validateEmail(String emailHashKey);
}
