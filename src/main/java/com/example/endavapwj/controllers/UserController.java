package com.example.endavapwj.controllers;

import com.example.endavapwj.DTOs.UserDTO;
import com.example.endavapwj.services.UserService.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v2/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public CompletableFuture<ResponseEntity<UserDTO>> me(){
        return userService.me().thenApply(ResponseEntity::ok);
    }
}
