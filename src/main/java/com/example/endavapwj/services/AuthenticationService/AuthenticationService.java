package com.example.endavapwj.services.AuthenticationService;

import com.example.endavapwj.DTOs.LoginDTO;
import com.example.endavapwj.DTOs.RegisterDTO;
import com.example.endavapwj.exceptions.AccountLockedException;
import com.example.endavapwj.exceptions.AlreadyExistsException;
import com.example.endavapwj.exceptions.InvalidFieldException;
import jakarta.transaction.Transactional;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Service implementation responsible for user authentication and account lifecycle operations.
 * Handles registration, email validation, and login with password verification and throttling.
 */
public interface AuthenticationService {

    /**
     * Handles the logic of registration.
     * <p>
     * Checks whether an user already exists. <p>
     * Creates a new user entry with an encoded password and generates an email validation token.
     *
     * @param registerDTO data required to create a new user
     * @return a future containing a response message and email validation token
     * @throws AlreadyExistsException if the username or email is already registered
     */
    @Transactional
    CompletableFuture<Map<String, String>> registerUser(RegisterDTO registerDTO);
    /**
     * Confirms a user's email address.
     * Looks up a matching validation token and marks the user email as verified.
     * Removes the consumed validation token.
     *
     * @param emailHashKey email validation token
     * @return a future containing a confirmation message
     * @throws InvalidFieldException if the validation token is not found
     */
    @Transactional
    CompletableFuture<Map<String,String>> validateEmail(String emailHashKey);
    /**
     * Attempts to authenticate a user.
     * Verifies that the user exists, checks for account lockout, and validates the password.
     * Generates both access and refresh JWT tokens on successful authentication.
     *
     * @param loginDTO credentials submitted by the user
     * @return a future containing authentication success message and issued tokens
     * @throws InvalidFieldException if credentials are invalid
     * @throws AccountLockedException if the account is temporarily locked due to repeated failed attempts
     */
    @Transactional
    CompletableFuture<Map<String,String>> login(LoginDTO loginDTO);
}
