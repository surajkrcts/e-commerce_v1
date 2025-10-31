package com.genc.e_commerce.controller;

import com.genc.e_commerce.dto.LoginRequest;
import com.genc.e_commerce.dto.LoginResponse;
import com.genc.e_commerce.dto.UserUpdateDTO;
import com.genc.e_commerce.entity.User;
import com.genc.e_commerce.exception.ResourceNotFoundException;
import com.genc.e_commerce.service.UserService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UserController {

    private static final Logger logger = LogManager.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register-user")
    public ResponseEntity<Map<String, Object>> registerUser(@Valid @RequestBody User user) {
        logger.info("Request received to register a new user with username: {}", user.getUsername());
        User newUser = userService.addUser(user);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("data", newUser);
        logger.info("User '{}' registered successfully with ID: {}", newUser.getUsername(), newUser.getUserId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login-user")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Login attempt for user: {}", loginRequest.getUsername());
        User userData = userService.loginUser(loginRequest);
        LoginResponse loginResponse = new LoginResponse(
                userData.getUserId(),
                userData.getUsername(),
                userData.getRole());
        logger.info("User '{}' logged in successfully.", userData.getUsername());
        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/get-user-details/{userId}")
    public ResponseEntity<Map<String, Object>> getUserProfile(@PathVariable Long userId) {
        logger.info("Request received to fetch profile for user ID: {}", userId);
        User user = userService.getUserProfile(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found."));
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User data fetched successfully");
        response.put("data", user);
        logger.info("Successfully fetched profile for user ID: {}", userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-user-data/{userId}")
    public ResponseEntity<Map<String, Object>> updateUserProfile(
            @PathVariable Long userId,
            @Valid @RequestBody UserUpdateDTO userData) {
        logger.info("Request received to update profile for user ID: {}", userId);
        logger.debug("Update payload for user ID {}: {}", userId, userData);
        User updatedUser = userService.updateUserProfile(userId, userData);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Profile updated successfully");
        response.put("data", updatedUser);
        logger.info("Profile updated successfully for user ID: {}", userId);
        return ResponseEntity.ok(response);
    }
}