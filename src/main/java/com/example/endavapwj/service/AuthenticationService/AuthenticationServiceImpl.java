package com.example.endavapwj.service.AuthenticationService;

import com.example.endavapwj.DTOs.RegisterDTO;
import com.example.endavapwj.collection.EmailValidation;
import com.example.endavapwj.collection.User;
import com.example.endavapwj.controllers.EmailValidationRepository;
import com.example.endavapwj.enums.Role;
import com.example.endavapwj.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UserRepository userRepository;
    private final EmailValidationRepository emailValidation;

    public AuthenticationServiceImpl(UserRepository userRepository,EmailValidationRepository emailValidation) {
        this.userRepository = userRepository;
        this.emailValidation = emailValidation;
    }

    @Override
    public CompletableFuture<Map<String, String>> registerUser(RegisterDTO registerDTO) {
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setEmail(registerDTO.getEmail());
        user.setRole(Role.User);
        this.userRepository.save(user);

        EmailValidation emailValidation = new EmailValidation();
        emailValidation.setEmail(registerDTO.getEmail());
        emailValidation.setEmailHash(RandomStringUtils.secure().nextAlphanumeric(24));
        this.emailValidation.save(emailValidation);
        return CompletableFuture.completedFuture(Map.ofEntries(Map.entry("message","Register successful"),Map.entry("email",emailValidation.getEmailHash())));
    }

    @Override
    public CompletableFuture<Map<String, String>> validateEmail(String emailHashKey) {
        return null;
    }
}
