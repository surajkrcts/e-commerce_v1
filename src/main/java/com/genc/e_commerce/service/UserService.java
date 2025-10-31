package com.genc.e_commerce.service;

import com.genc.e_commerce.dto.LoginRequest;
import com.genc.e_commerce.dto.UserUpdateDTO;
import com.genc.e_commerce.entity.User;
import com.genc.e_commerce.exception.AuthFailedException;
import com.genc.e_commerce.exception.DuplicateResourceException;
import com.genc.e_commerce.exception.ResourceNotFoundException;
import com.genc.e_commerce.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LogManager.getLogger(UserService.class);


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor Injection
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // User Registration
    public User addUser(User user) {
        logger.info("Attempting to add new user with username: {}", user != null ? user.getUsername() : "null");

        // Validate input
        if (user == null || user.getUsername() == null || user.getUsername().isEmpty()) {
            throw new IllegalArgumentException("User and username must be provided.");
        }

        // Encode the password before saving
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // Check for existing user
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            logger.warn("Registration failed: User with username '{}' already exists.", user.getUsername());
            throw new DuplicateResourceException("User with username '" + user.getUsername() + "' already exists.");
        }

        // Save the new user
        User savedUser = userRepository.save(user);
        logger.info("Successfully added user with ID: {}", savedUser.getUserId());
        return savedUser;
    }

    // User Login
    public User loginUser(LoginRequest loginRequest) {
        if (loginRequest == null || loginRequest.getUsername() == null) {
            throw new IllegalArgumentException("LoginRequest and username must be provided.");
        }

        String username = loginRequest.getUsername();
        logger.info("Attempting login for user: {}", username);

        Optional<User> userDetails = userRepository.findByUsername(username);
        if (userDetails.isEmpty()) {
            logger.warn("Login failed for user: {} (not found)", username);
            throw new AuthFailedException("Invalid username or password.");
        }

        User user = userDetails.get();
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            logger.warn("Login failed for user: {} (bad credentials)", username);
            throw new AuthFailedException("Invalid username or password.");
        }

        logger.info("Successful login for user ID: {}", user.getUserId());
        return user;
    }

    public Optional<User> getUserProfile(Long userId) {
        logger.debug("Fetching profile for user ID: {}", userId);
        return userRepository.findById(userId);
    }

    public User updateUserProfile(Long userId, UserUpdateDTO request) {
        Optional<User> userDetailsFromDB = userRepository.findById(userId);

        if (userDetailsFromDB.isEmpty()) {
            logger.warn("Update failed: User with ID {} not found.", userId);
            throw new ResourceNotFoundException("User with id " + userId + " not found.");
        }

        User existingUser = userDetailsFromDB.get();

        if (request.getUsername() != null && !request.getUsername().isBlank() && !existingUser.getUsername().equals(request.getUsername())) {
            existingUser.setUsername(request.getUsername());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank() && !existingUser.getEmail().equals(request.getEmail())) {
            existingUser.setEmail(request.getEmail());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            String encodedPassword = this.passwordEncoder.encode(request.getPassword());
            existingUser.setPassword(encodedPassword);
            logger.info("Password successfully reset for user ID: {}", userId);
        }

        return userRepository.save(existingUser);
    }
}