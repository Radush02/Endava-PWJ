package com.example.endavapwj.controllers;

import com.example.endavapwj.DTOs.AuthenticationDTO.LoginDTO;
import com.example.endavapwj.DTOs.AuthenticationDTO.LoginResultDTO;
import com.example.endavapwj.DTOs.AuthenticationDTO.RegisterDTO;
import com.example.endavapwj.services.AuthenticationService.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v2/auth")
public class AuthenticationController {
  private final AuthenticationService authenticationService;
  @Value("${security.cookie.secure}")
  private boolean cookieSecure;

  @Value("${security.cookie.same-site}")
  private String cookieSameSite;

  public AuthenticationController(AuthenticationService authenticationService) {
    this.authenticationService = authenticationService;
  }

  @PostMapping("/register")
  public CompletableFuture<ResponseEntity<Map<String, String>>> register(
      @Valid @RequestBody RegisterDTO registerDTO) {
    return authenticationService
        .registerUser(registerDTO)
        .thenApply(body -> ResponseEntity.status(HttpStatus.CREATED).body(body));
  }

  @PostMapping("/validate/{emailHashKey}")
  public CompletableFuture<ResponseEntity<Map<String, String>>> validate(
      @PathVariable String emailHashKey) {
    return authenticationService
        .validateEmail(emailHashKey)
        .thenApply(body -> ResponseEntity.status(HttpStatus.CREATED).body(body));
  }

  @PostMapping("/login")
  public CompletableFuture<ResponseEntity<LoginResultDTO>> login(
      @Valid @RequestBody LoginDTO loginDTO, HttpServletResponse response) {
    return authenticationService
        .login(loginDTO)
        .thenApply(
            body -> {
              String accessToken = body.getAccessToken();
              String refreshToken = body.getRefreshToken();
              ResponseCookie jwtCookie =
                  ResponseCookie.from("jwt", accessToken)
                      .httpOnly(true)
                      .secure(cookieSecure)
                      .path("/")
                      .maxAge(12 * 60 * 60)
                      .sameSite(cookieSameSite)
                      .build();
              ResponseCookie refreshCookie =
                  ResponseCookie.from("refresh", refreshToken)
                      .httpOnly(true)
                      .secure(cookieSecure)
                      .path("/")
                      .maxAge(60L * 60L * 24L * 30L)
                      .sameSite(cookieSameSite)
                      .build();

              response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
              response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
              return ResponseEntity.status(HttpStatus.CREATED).body(body);
            });
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpServletResponse response) {
    ResponseCookie jwtCookie =
        ResponseCookie.from("jwt", "")
            .httpOnly(true)
            .secure(cookieSecure)
            .path("/")
            .maxAge(0)
            .sameSite(cookieSameSite)
            .build();

    ResponseCookie refreshCookie =
        ResponseCookie.from("refresh", "")
            .httpOnly(true)
            .secure(cookieSecure)
            .path("/")
            .maxAge(0)
            .sameSite(cookieSameSite)
            .build();

    response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
    response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

    return ResponseEntity.ok(Map.of("response", "Logged out successfully"));
  }

  @GetMapping
  public CompletableFuture<ResponseEntity<Map<String, String>>> loggedIn(){
      return authenticationService.loggedIn().thenApply((body)->ResponseEntity.status(HttpStatus.OK).body(body));
  }
}
