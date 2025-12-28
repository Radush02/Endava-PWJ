package com.example.endavapwj.services.UserService;

import com.example.endavapwj.DTOs.UserDTO.UpdateUserDTO;
import com.example.endavapwj.DTOs.UserDTO.UserDTO;
import com.example.endavapwj.collection.User;
import com.example.endavapwj.exceptions.InvalidFieldException;
import com.example.endavapwj.exceptions.NotPermittedException;
import com.example.endavapwj.repositories.UserRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

  private UserRepository userRepository;

  @Override
  public CompletableFuture<UserDTO> me() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    User u =
        userRepository
            .findByUsernameIgnoreCase(username)
            .orElseThrow(() -> new InvalidFieldException("Invalid account details."));
    return CompletableFuture.completedFuture(
        UserDTO.builder()
            .id(u.getId())
            .username(u.getUsername())
            .email(u.getEmail())
            .image(u.getImage())
            .role(u.getRole())
            .fullName(u.getFullName())
            .build());
  }

  @Override
  public CompletableFuture<Map<String, String>> update(UpdateUserDTO updateUserDTO) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    User u =
        userRepository
            .findByUsernameIgnoreCase(username)
            .orElseThrow(() -> new InvalidFieldException("Invalid account details."));
    u.setEmail(updateUserDTO.getEmail() != null ? updateUserDTO.getEmail() : u.getEmail());
    u.setUsername(updateUserDTO.getUsername() != null ? updateUserDTO.getUsername() : username);
    u.setFullName(
        updateUserDTO.getFullName() != null ? updateUserDTO.getFullName() : u.getFullName());
    if (updateUserDTO.getImage() != null) {
      try {
        String extension =
            updateUserDTO
                .getImage()
                .getOriginalFilename()
                .substring(updateUserDTO.getImage().getOriginalFilename().lastIndexOf("."));
        String uploadPath = "/var/www/radush.ro/current/" + username + extension;
        byte[] bytes = updateUserDTO.getImage().getBytes();
        Files.write(Paths.get(uploadPath), bytes);
        u.setImage("https://radush.ro/static/" + username + extension);
      } catch (IOException e) {
        throw new NotPermittedException("Image upload failed.");
      }
    }
    userRepository.save(u);
    return CompletableFuture.completedFuture(Map.of("message", "Profile updated successfully"));
  }
}
