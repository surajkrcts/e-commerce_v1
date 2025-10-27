package com.genc.e_commerce.controller;

import com.genc.e_commerce.dto.LoginRequest;
import com.genc.e_commerce.dto.LoginResponse;
import com.genc.e_commerce.dto.UserUpdateDTO;
import com.genc.e_commerce.entity.User;
import com.genc.e_commerce.service.UserService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UserController {
    private static final Logger logger = LogManager.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/register-user")
    public ResponseEntity<Map<String, Object>> registerUser(@Valid @RequestBody User user) {
        logger.info("Request received to register a new user with username: {}", user.getUsername());
        Map<String, Object> response = new HashMap<>();
        try {
            User newUser = userService.addUser(user);
            response.put("message", "User registered successfully");
            response.put("data", newUser);
            logger.info("User '{}' registered successfully with ID: {}", newUser.getUsername(), newUser.getUserId());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            logger.warn("Registration failed for username '{}' because it already exists.", user.getUsername());
            response.put("error", "Username or email already exists.");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        } catch (Exception e) {
            logger.error("An unexpected error occurred during user registration for username '{}'", user.getUsername(), e);
            response.put("error", "Server error during registration.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login-user")
    public ResponseEntity<Object> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Login attempt for user: {}", loginRequest.getUsername());
        try {
            User userData = userService.loginUser(loginRequest);
            LoginResponse loginResponse = new LoginResponse(
                    userData.getUserId(),
                    userData.getUsername(),
                    userData.getRole());
            logger.info("User '{}' logged in successfully.", userData.getUsername());
            return ResponseEntity.ok(loginResponse);
        } catch (RuntimeException e) {
            logger.warn("Failed login attempt for user: {}. Reason: {}", loginRequest.getUsername(), e.getMessage());
            Map<String, String> response = new HashMap<>();
            response.put("error", "Invalid username or password.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/get-user-details/{userId}")
    public ResponseEntity<Map<String, Object>>getUserProfile(@PathVariable Long userId) {
        logger.info("Request received to fetch profile for user ID: {}", userId);
        Map<String, Object> response = new HashMap<>();
        Optional<User> userOptional = userService.getUserProfile(userId);

        if (userOptional.isPresent()) {
            logger.info("Successfully fetched profile for user ID: {}", userId);
            response.put("message", "User data fetched successfully");
            response.put("data", userOptional.get());
            return ResponseEntity.ok(response);
        } else {
            logger.warn("User profile not found for ID: {}", userId);
            response.put("error", "User with ID " + userId + " not found.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update-user-data/{userId}")
    public ResponseEntity<Map<String, Object>> updateUserProfile(@PathVariable Long userId,@Valid @RequestBody UserUpdateDTO userData) {
        logger.info("Request received to update profile for user ID: {}", userId);
        logger.debug("Update payload for user ID {}: {}", userId, userData);
        Map<String, Object> response = new HashMap<>();
        try {
            User updatedUser = userService.updateUserProfile(userId, userData);
            response.put("message", "Profile updated successfully");
            response.put("data", updatedUser);
            logger.info("Profile updated successfully for user ID: {}", userId);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            logger.warn("Attempted to update a non-existent user profile with ID: {}", userId);
            response.put("error", "User with ID " + userId + " not found.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error updating profile for user ID: {}", userId, e);
            response.put("error", "An unexpected error occurred during profile update.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}