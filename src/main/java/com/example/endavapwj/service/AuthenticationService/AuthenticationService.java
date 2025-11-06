package com.example.endavapwj.service.AuthenticationService;

import com.example.endavapwj.DTOs.LoginDTO;
import com.example.endavapwj.DTOs.RegisterDTO;
import jakarta.transaction.Transactional;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface AuthenticationService {

    @Transactional
    CompletableFuture<Map<String, String>> registerUser(RegisterDTO registerDTO);

    @Transactional
    CompletableFuture<Map<String,String>> validateEmail(String emailHashKey);

    @Transactional
    CompletableFuture<Map<String,String>> login(LoginDTO loginDTO);
}
