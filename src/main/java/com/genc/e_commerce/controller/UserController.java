package com.genc.e_commerce.controller;

import com.genc.e_commerce.dto.LoginRequest;
import com.genc.e_commerce.dto.LoginResponse;
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

// @RestController marks this class as a Spring REST controller, ready to handle web requests.
@RestController
// @RequestMapping sets the base URL for all endpoints in this controller to "/api".
@RequestMapping("/api")
// @CrossOrigin allows requests from any origin, which is crucial for frontend integration.
@CrossOrigin(origins = "*")
public class UserController {
    // Initializes a logger for this class.
    private static final Logger logger = LogManager.getLogger(UserController.class);

    // @Autowired injects the UserService to handle the business logic for users.
    @Autowired
    private UserService userService;

    /**
     * Endpoint for registering a new user.
     * @param user The user data from the request body. @Valid triggers bean validation.
     * @return A ResponseEntity containing a success message and user data, or an error.
     */
    @PostMapping("/register-user")
    public ResponseEntity<Map<String, Object>> registerUser(@Valid @RequestBody User user) {
        logger.info("Request received to register a new user with username: {}", user.getUsername());
        // A map is used to create a flexible JSON response.
        Map<String, Object> response = new HashMap<>();
        try {
            // Call the service layer to create the new user.
            User newUser = userService.addUser(user);
            response.put("message", "User registered successfully");
            response.put("data", newUser);
            logger.info("User '{}' registered successfully with ID: {}", newUser.getUsername(), newUser.getUserId());
            // Return a 201 CREATED status on success.
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            // Catch exceptions related to database constraints, like a unique username violation.
            logger.warn("Registration failed for username '{}' because it already exists.", user.getUsername());
            response.put("error", "Username or email already exists.");
            // Return a 409 CONFLICT status, which is appropriate for duplicate resources.
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        } catch (Exception e) {
            // Catch any other unexpected server-side errors.
            logger.error("An unexpected error occurred during user registration for username '{}'", user.getUsername(), e);
            response.put("error", "Server error during registration.");
            // Return a 500 INTERNAL_SERVER_ERROR status.
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint for user login.
     * @param loginRequest DTO containing the user's username and password.
     * @return A ResponseEntity with a LoginResponse DTO on success, or an error message.
     */
    @PostMapping("/login-user")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        logger.info("Login attempt for user: {}", loginRequest.getUsername());
        try {
            // Call the service to authenticate the user.
            User userData = userService.loginUser(loginRequest);
            // Create a specific response DTO for login, exposing only necessary information.
            LoginResponse loginResponse = new LoginResponse(
                    userData.getUserId(),
                    userData.getUsername(),
                    userData.getRole());
            logger.info("User '{}' logged in successfully.", userData.getUsername());
            // Return a 200 OK status with the login response.
            return ResponseEntity.ok(loginResponse);
        } catch (RuntimeException e) { // Catches exceptions from the service for failed login attempts.
            logger.warn("Failed login attempt for user: {}. Reason: {}", loginRequest.getUsername(), e.getMessage());
            Map<String, String> response = new HashMap<>();
            response.put("error", "Invalid username or password.");
            // Return a 401 UNAUTHORIZED status for failed authentication.
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Endpoint to retrieve a user's profile information.
     * @param userId The ID of the user to fetch, from the URL path.
     * @return A ResponseEntity with the user's data or a not-found error.
     */
    @GetMapping("/get-user-details/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long userId) {
        logger.info("Request received to fetch profile for user ID: {}", userId);
        Map<String, Object> response = new HashMap<>();
        // Call the service to find the user by their ID.
        Optional<User> userOptional = userService.getUserProfile(userId);

        // Check if the user was found.
        if (userOptional.isPresent()) {
            logger.info("Successfully fetched profile for user ID: {}", userId);
            response.put("message", "User data fetched successfully");
            response.put("data", userOptional.get());
            // Return 200 OK with the user data.
            return ResponseEntity.ok(response);
        } else {
            logger.warn("User profile not found for ID: {}", userId);
            response.put("error", "User with ID " + userId + " not found.");
            // Return 404 NOT_FOUND if the user does not exist.
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Endpoint to update a user's profile.
     * @param userId The ID of the user to update.
     * @param userData The new user data from the request body.
     * @return A ResponseEntity with the updated user data or an error message.
     */
    @PutMapping("/update-user-data/{userId}")
    public ResponseEntity<Map<String, Object>> updateUserProfile(@PathVariable Long userId, @RequestBody User userData) {
        logger.info("Request received to update profile for user ID: {}", userId);
        logger.debug("Update payload for user ID {}: {}", userId, userData);
        Map<String, Object> response = new HashMap<>();
        try {
            // Call the service to perform the update.
            User updatedUser = userService.updateUserProfile(userId, userData);
            response.put("message", "Profile updated successfully");
            response.put("data", updatedUser);
            logger.info("Profile updated successfully for user ID: {}", userId);
            // Return 200 OK with the updated data.
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            // Catch exception if the service indicates the user to update was not found.
            logger.warn("Attempted to update a non-existent user profile with ID: {}", userId);
            response.put("error", "User with ID " + userId + " not found.");
            // Return 404 NOT_FOUND.
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // Catch any other unexpected errors.
            logger.error("Error updating profile for user ID: {}", userId, e);
            response.put("error", "An unexpected error occurred during profile update.");
            // Return 500 INTERNAL_SERVER_ERROR.
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}