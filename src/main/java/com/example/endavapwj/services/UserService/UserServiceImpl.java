package com.example.endavapwj.services.UserService;

import com.example.endavapwj.DTOs.UserDTO;
import com.example.endavapwj.collection.User;
import com.example.endavapwj.exceptions.InvalidFieldException;
import com.example.endavapwj.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    @Override
    public CompletableFuture<UserDTO> me() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User u = userRepository.findByUsernameIgnoreCase(username).orElseThrow(()->new InvalidFieldException("Invalid account details."));
        return CompletableFuture.completedFuture(UserDTO.builder()
                .id(u.getId())
                .username(u.getUsername())
                .email(u.getEmail())
                .image(u.getImage())
                .role(u.getRole())
                .fullName(u.getFullName())
                .build());
    }
}
