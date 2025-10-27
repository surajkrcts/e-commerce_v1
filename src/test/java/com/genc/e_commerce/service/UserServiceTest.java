package com.genc.e_commerce.service;

import com.genc.e_commerce.dto.LoginRequest;
import com.genc.e_commerce.entity.User;
import com.genc.e_commerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    // 1. Mock the repository dependency
    @Mock
    private UserRepository userRepository;

    // 2. Inject mocks into the service.
    // We will manually set the password encoder since it's created in the constructor.
    @InjectMocks
    private UserService userService;

    private User testUser;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 3. Set up common test data before each test run
    @BeforeEach
    void setUp() {
        // Manually set the real encoder in the service instance for consistent testing
        userService.passwordEncoder = this.passwordEncoder;

        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        // Store the encoded version of "password123"
        testUser.setPassword(passwordEncoder.encode("password123"));
    }

    @Test
    void addUser_whenUsernameIsNew_shouldSaveAndReturnUser() {
        // --- ARRANGE ---
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password123"); // Raw password

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty()); // No existing user
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // --- ACT ---
        User savedUser = userService.addUser(newUser);

        // --- ASSERT ---
        assertNotNull(savedUser);
        assertEquals("newuser", savedUser.getUsername());
        // Verify the password was encoded
        assertTrue(passwordEncoder.matches("password123", savedUser.getPassword()));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void addUser_whenUsernameExists_shouldThrowRuntimeException() {
        // --- ARRANGE ---
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // --- ACT & ASSERT ---
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            // Create a new user object with the same username to simulate the request
            User conflictingUser = new User();
            conflictingUser.setUsername("testuser");
            // FIX: Added a password to prevent the "rawPassword cannot be null" error
            conflictingUser.setPassword("any-password-will-do");
            userService.addUser(conflictingUser);
        });
        assertEquals("User with username 'testuser' already exists!", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginUser_withCorrectCredentials_shouldReturnUser() {
        // --- ARRANGE ---
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // --- ACT ---
        User loggedInUser = userService.loginUser(loginRequest);

        // --- ASSERT ---
        assertNotNull(loggedInUser);
        assertEquals("testuser", loggedInUser.getUsername());
    }

    @Test
    void loginUser_withIncorrectPassword_shouldThrowException() {
        // --- ARRANGE ---
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("wrongpassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // --- ACT & ASSERT ---
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.loginUser(loginRequest);
        });
        assertEquals("not found", exception.getMessage());
    }

    @Test
    void loginUser_withNonExistentUser_shouldThrowException() {
        // --- ARRANGE ---
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("nouser");
        loginRequest.setPassword("password123");
        when(userRepository.findByUsername("nouser")).thenReturn(Optional.empty());

        // --- ACT & ASSERT ---
        assertThrows(RuntimeException.class, () -> userService.loginUser(loginRequest));
    }

    @Test
    void getUserProfile_whenUserExists_shouldReturnUser() {
        // --- ARRANGE ---
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // --- ACT ---
        Optional<User> userProfile = userService.getUserProfile(1L);

        // --- ASSERT ---
        assertTrue(userProfile.isPresent());
        assertEquals("testuser", userProfile.get().getUsername());
    }

    @Test
    void updateUserProfile_whenUserExists_shouldUpdateAndReturnUser() {
        // --- ARRANGE ---
        User updateInfo = new User();
        updateInfo.setUsername("updateduser");
        updateInfo.setEmail("updated@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // --- ACT ---
        User updatedUser = userService.updateUserProfile(1L, updateInfo);

        // --- ASSERT ---
        assertNotNull(updatedUser);
        assertEquals("updateduser", updatedUser.getUsername());
        assertEquals("updated@example.com", updatedUser.getEmail());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void updateUserProfile_whenUserNotFound_shouldReturnNull() {
        // --- ARRANGE ---
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // --- ACT ---
        User result = userService.updateUserProfile(99L, new User());

        // --- ASSERT ---
        assertNull(result);
        verify(userRepository, never()).save(any(User.class));
    }
}