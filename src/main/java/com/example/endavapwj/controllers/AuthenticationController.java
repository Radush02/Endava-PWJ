package com.example.endavapwj.controllers;


import com.example.endavapwj.DTOs.LoginDTO;
import com.example.endavapwj.DTOs.RegisterDTO;
import com.example.endavapwj.services.AuthenticationService.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("api/v2/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public CompletableFuture<ResponseEntity<Map<String,String>>> register(@Valid @RequestBody RegisterDTO registerDTO){
        return authenticationService.registerUser(registerDTO).thenApply(body->ResponseEntity.status(HttpStatus.CREATED).body(body));
    }

    @PostMapping("/validate")
    public CompletableFuture<ResponseEntity<Map<String,String>>> validate(@RequestParam String emailHashKey){
        return authenticationService.validateEmail(emailHashKey).thenApply(body->ResponseEntity.status(HttpStatus.CREATED).body(body));
    }
    @PostMapping("/login")
    public CompletableFuture<ResponseEntity<Map<String,String>>> login(@Valid @RequestBody LoginDTO loginDTO, HttpServletResponse response){
        return authenticationService.login(loginDTO).thenApply(
                body->
                {
                    String accessToken = body.get("access");
                    String refreshToken = body.get("refresh");
                    ResponseCookie jwtCookie = ResponseCookie.from("jwt", accessToken)
                            .httpOnly(true)
                            .secure(true)
                            .path("/")
                            .maxAge(12* 60 * 60)
                            .sameSite("Strict")
                            .build();
                    ResponseCookie refreshCookie = ResponseCookie.from("refresh", refreshToken)
                            .httpOnly(true)
                            .secure(true)
                            .path("/")
                            .maxAge(60L * 60L * 24L * 30L)
                            .sameSite("Strict")
                            .build();

                    response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
                    response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
                    Map<String,String> resp = new HashMap<>();
                    resp.put("message",body.get("message"));
                    return ResponseEntity.status(HttpStatus.CREATED).body(resp);
                }
        );
    }
}
