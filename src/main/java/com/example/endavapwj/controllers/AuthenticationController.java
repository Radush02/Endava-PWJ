package com.example.endavapwj.controllers;


import com.example.endavapwj.DTOs.RegisterDTO;
import com.example.endavapwj.service.AuthenticationService.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("api/v2/auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/register")
    public CompletableFuture<ResponseEntity<Map<String,String>>> register(@Valid @RequestBody RegisterDTO registerDTO){
        return authenticationService.registerUser(registerDTO).thenApply(body->ResponseEntity.status(HttpStatus.CREATED).body(body));
    }

    @PostMapping("/validate")
    public CompletableFuture<ResponseEntity<Map<String,String>>> validate(@RequestParam String emailHashKey){
        return authenticationService.validateEmail(emailHashKey).thenApply(body->ResponseEntity.status(HttpStatus.CREATED).body(body));
    }
}
