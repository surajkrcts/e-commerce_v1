package com.genc.e_commerce.service;

import com.genc.e_commerce.dto.LoginRequest;
import com.genc.e_commerce.entity.User;
import com.genc.e_commerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

// @Service annotation marks this class as a Spring service component, making it available for dependency injection.
@Service
public class UserService {

    // @Autowired injects an instance of UserRepository, allowing the service to interact with the database.
    @Autowired
    private UserRepository userRepository;

    // Declares a PasswordEncoder to handle password hashing.
    PasswordEncoder passwordEncoder;

    // The constructor for the service.
    public UserService() {
        // Initializes the passwordEncoder with BCrypt, a strong hashing algorithm.
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Registers a new user in the system.
     * It encodes the user's password and checks for duplicate usernames before saving.
     * @param user The User object containing the new user's details.
     * @return The saved User object with an encoded password.
     */
    public User addUser(User user) {
        // Encodes the raw password from the user object for secure storage.
        String encodedPasswod = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPasswod);

        // Checks if a user with the same username already exists in the database.
        Optional<User> existingUser = this.userRepository.findByUsername(user.getUsername());

        // If a user with that username is found, throw an exception to prevent duplicates.
        if (existingUser.isPresent()) {
            throw new RuntimeException("User with username '" + user.getUsername() + "' already exists!");
        }
        // If the username is unique, save the new user to the database.
        return userRepository.save(user);
    }

    /**
     * Authenticates a user based on their username and password.
     * @param loginRequest A DTO containing the username and raw password.
     * @return The User object if authentication is successful.
     * @throws RuntimeException if the user is not found or the password does not match.
     */
    public User loginUser(LoginRequest loginRequest) throws RuntimeException {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        // Tries to find the user by their username in the database.
        Optional<User> userDetails = userRepository.findByUsername(username);

        // Checks if the user was found.
        if (userDetails.isPresent()) {
            User user = userDetails.get();
            // Gets the securely stored, encoded password from the database.
            String storedEncodedPassword = user.getPassword();

            // Compares the raw password from the login request with the stored encoded password.
            // .matches() handles the comparison securely without decoding the stored password.
            if (passwordEncoder.matches(password, storedEncodedPassword)) {
                // If passwords match, return the user object.
                return userDetails.get();
            }
        }
        // Throws an exception if the user is not found or if the password does not match.
        throw new RuntimeException("not found");
    }

    /**
     * Retrieves the profile of a user by their ID.
     * @param userId The unique ID of the user.
     * @return An Optional containing the User object if found, otherwise an empty Optional.
     */
    public Optional<User> getUserProfile(Long userId) {
        // Finds the user in the database by their primary key (userId).
        Optional<User> userDetails = userRepository.findById(userId);

        // If a user is found with the given ID.
        if (userDetails.isPresent()) {
            User user = userDetails.get();
            String storedEncodedPassword = user.getPassword();
            // NOTE: This line compares the encoded password with itself, which will always be true.
            // The semicolon at the end means it's an empty statement that does nothing.
            if (passwordEncoder.matches(user.getPassword(), storedEncodedPassword)) ;
            // Returns the found user details.
            return userDetails;
        }
        // If no user is found, return an empty Optional.
        return Optional.empty();
    }

    /**
     * Updates the profile information for an existing user.
     * @param userId The ID of the user to update.
     * @param user   A User object containing the new profile information.
     * @return The updated User object, or null if the user was not found.
     */
    public User updateUserProfile(Long userId, User user) {
        // Finds the existing user in the database.
        Optional<User> userDetailsFromDB = userRepository.findById(userId);

        // Checks if the user exists.
        if (userDetailsFromDB.isPresent()) {
            // Gets the existing user object from the database.
            User existingUser = userDetailsFromDB.get();

            // Updates the fields of the existing user with the new information.
            existingUser.setUsername(user.getUsername());
            existingUser.setEmail(user.getEmail());
            existingUser.setRole(user.getRole());

            // Saves the updated user back to the database.
            return userRepository.save(existingUser);
        }
        // If the user to update is not found, return null.
        return null;
    }
}