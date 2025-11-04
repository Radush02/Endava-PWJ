package com.example.endavapwj.controllers;


import com.example.endavapwj.DTOs.RegisterDTO;
import com.example.endavapwj.service.AuthenticationService.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    public ResponseEntity<Map<String,String>> register(@RequestBody RegisterDTO registerDTO) throws ExecutionException, InterruptedException {
        Map<String,String> response;
        response = authenticationService.registerUser(registerDTO).get();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
