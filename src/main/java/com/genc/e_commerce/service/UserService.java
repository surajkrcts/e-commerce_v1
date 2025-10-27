package com.genc.e_commerce.service;

import com.genc.e_commerce.dto.LoginRequest;
import com.genc.e_commerce.dto.UserUpdateDTO;
import com.genc.e_commerce.entity.User;
import com.genc.e_commerce.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
public class UserService {

    // Correctly initialize the logger using SLF4J factory
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    public UserService() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }


    public User addUser(User user) {
        // Logging the attempt to add a new user
        logger.info("Attempting to add new user with username: {}", user.getUsername());

        String encodedPasswod = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPasswod);
        Optional<User> existingUser = this.userRepository.findByUsername(user.getUsername());

        if (existingUser.isPresent()) {
            logger.warn("Registration failed: User with username '{}' already exists.", user.getUsername());
            throw new RuntimeException("User with username '" + user.getUsername() + "' already exists!");
        }
        User savedUser = userRepository.save(user);
        logger.info("Successfully added user with ID: {}", savedUser.getUserId());
        return savedUser;
    }

    public User loginUser(LoginRequest loginRequest) throws RuntimeException {
        String username = loginRequest.getUsername();
        logger.info("Attempting login for user: {}", username);

        String password = loginRequest.getPassword();
        Optional<User> userDetails = userRepository.findByUsername(username);

        if (userDetails.isPresent()) {
            User user = userDetails.get();
            String storedEncodedPassword = user.getPassword();

            if (passwordEncoder.matches(password, storedEncodedPassword)) {
                logger.info("Successful login for user ID: {}", user.getUserId());
                return user;
            }
        }
        logger.warn("Login failed for user: {}", username);
        throw new RuntimeException("Invalid username or password.");
    }

    public Optional<User> getUserProfile(Long userId) {
        logger.debug("Fetching profile for user ID: {}", userId);
        return userRepository.findById(userId);
    }


    public User updateUserProfile(Long userId, UserUpdateDTO request) {
        Optional<User> userDetailsFromDB = userRepository.findById(userId);

        if (userDetailsFromDB.isPresent()) {
            User existingUser = userDetailsFromDB.get();

            // Update Username
            if (request.getUsername() != null && !request.getUsername().isEmpty() && !existingUser.getUsername().equals(request.getUsername())) {
                existingUser.setUsername(request.getUsername());
            }

            // Update Email
            if (request.getEmail() != null && !request.getEmail().isEmpty() && !existingUser.getEmail().equals(request.getEmail())) {
                existingUser.setEmail(request.getEmail());
            }

            //  Update Password
            if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                String encodedPassword = this.passwordEncoder.encode(request.getPassword());
                existingUser.setPassword(encodedPassword);
                logger.info("Password successfully reset for user ID: {}", userId);
            }

            return userRepository.save(existingUser);
        }
        return null;
    }}