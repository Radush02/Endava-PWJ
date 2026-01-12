package com.example.endavapwj.controllers;

import com.example.endavapwj.DTOs.UserDTO.OtherUserDTO;
import com.example.endavapwj.DTOs.UserDTO.UpdateUserDTO;
import com.example.endavapwj.DTOs.UserDTO.UserDTO;
import com.example.endavapwj.exceptions.InvalidFieldException;
import com.example.endavapwj.exceptions.NotPermittedException;
import com.example.endavapwj.services.UserService.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User")
@RestController
@RequestMapping("/api/v2/user")
@AllArgsConstructor
public class UserController {
  private final UserService userService;

  @Operation(
      summary = "Get current user info",
      description =
          "Gets the info of the currently logged in user. "
              + "Returns the user data. If the account details are invalid throws exception.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Current user fetched successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserDTO.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid account details",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = InvalidFieldException.class)))
      })
  @GetMapping("/me")
  public CompletableFuture<ResponseEntity<UserDTO>> me() {
    return userService.me().thenApply(ResponseEntity::ok);
  }

  @Operation(
      summary = "Update current user profile",
      description =
          "Updates the profile of the currently logged in user. "
              + "Can change username, email, full name and profile image. "
              + "If a field is null it will keep the old value. "
              + "Returns a message when profile is updated. "
              + "If account details are invalid or image upload failed throws exception.",
      responses = {
        @ApiResponse(
            responseCode = "202",
            description = "Profile update accepted",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Map.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid account details",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = InvalidFieldException.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Image upload failed or not permitted",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = NotPermittedException.class)))
      })
  @PutMapping("/update")
  public CompletableFuture<ResponseEntity<Map<String, String>>> update(
      @RequestBody UpdateUserDTO updateUserDTO) {
    return userService
        .update(updateUserDTO)
        .thenApply((body) -> ResponseEntity.status(HttpStatus.ACCEPTED).body(body));
  }

  @Operation(
      summary = "Get info about another user",
      description =
          "Gets public info about another user by username. "
              + "Returns username, image and role. "
              + "If the user is not logged in or the requested user doesn't exist throws exception.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Other user fetched successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = OtherUserDTO.class))),
        @ApiResponse(
            responseCode = "400",
            description = "User not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = InvalidFieldException.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Not logged in",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = NotPermittedException.class)))
      })
  @GetMapping("/user/{username}")
  public CompletableFuture<ResponseEntity<OtherUserDTO>> user(@PathVariable String username) {
    return userService
        .info(username)
        .thenApply((body) -> ResponseEntity.status(HttpStatus.OK).body(body));
  }
}
