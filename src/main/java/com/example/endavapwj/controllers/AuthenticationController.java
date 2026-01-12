package com.example.endavapwj.controllers;

import com.example.endavapwj.DTOs.AuthenticationDTO.LoginDTO;
import com.example.endavapwj.DTOs.AuthenticationDTO.LoginResultDTO;
import com.example.endavapwj.DTOs.AuthenticationDTO.RegisterDTO;
import com.example.endavapwj.exceptions.AccountLockedException;
import com.example.endavapwj.exceptions.AlreadyExistsException;
import com.example.endavapwj.exceptions.InvalidFieldException;
import com.example.endavapwj.exceptions.NotPermittedException;
import com.example.endavapwj.services.AuthenticationService.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "All endpoints for login, register and user session")
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
  @Operation(
      summary = "Register new account",
      description =
          "Create new user account. On success returns message that register is successful "
              + "Invalid request body or already existing username or email will throw exceptions.",
      responses = {
        @ApiResponse(
            responseCode = "201",
            description = "Register successful",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Map.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid register data",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = MethodArgumentNotValidException.class))),
        @ApiResponse(
            responseCode = "409",
            description = "Username or email already exists",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AlreadyExistsException.class)))
      })
  public CompletableFuture<ResponseEntity<Map<String, String>>> register(
      @Valid @RequestBody RegisterDTO registerDTO) {
    return authenticationService
        .registerUser(registerDTO)
        .thenApply(body -> ResponseEntity.status(HttpStatus.CREATED).body(body));
  }

  @PostMapping("/validate/{emailHashKey}")
  @Operation(
      summary = "Validate email account",
      description =
          "Takes email validation token from email link and validate the account. "
              + "Returns message confirming email was validated successfully. Invalid or expired token throws exception.",
      responses = {
        @ApiResponse(
            responseCode = "201",
            description = "Email validated successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Map.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid or expired email token",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = InvalidFieldException.class)))
      })
  public CompletableFuture<ResponseEntity<Map<String, String>>> validate(
      @PathVariable String emailHashKey) {
    return authenticationService
        .validateEmail(emailHashKey)
        .thenApply(body -> ResponseEntity.status(HttpStatus.CREATED).body(body));
  }

  @PostMapping("/login")
  @Operation(
      summary = "Login into account",
      description =
          "Logs into existing account. Returns body with account info and access and refresh tokens, "
              + "and also set them in cookies. Invalid account details or locked account throws exceptions.",
      responses = {
        @ApiResponse(
            responseCode = "201",
            description = "Successfully logged in",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = LoginResultDTO.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid account details",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = InvalidFieldException.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Account locked",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AccountLockedException.class)))
      })
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
  @Operation(
      summary = "Logout from account",
      description =
          "Logs out current logged in account by clearing jwt and refresh token cookies. "
              + "Returns simple message that logout succeed.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Logout succeed",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Map.class)))
      })
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
  @Operation(
      summary = "Check if logged in",
      description =
          "Check if there is user currently authenticated. Returns username when user is logged in, "
              + "otherwise throws exception.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "User is logged in",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Map.class))),
        @ApiResponse(
            responseCode = "403",
            description = "User not logged in",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = NotPermittedException.class)))
      })
  public CompletableFuture<ResponseEntity<Map<String, String>>> loggedIn() {
    return authenticationService
        .loggedIn()
        .thenApply(body -> ResponseEntity.status(HttpStatus.OK).body(body));
  }
}
