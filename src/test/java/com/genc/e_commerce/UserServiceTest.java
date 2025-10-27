package com.genc.e_commerce;

import com.genc.e_commerce.dto.LoginRequest;
import com.genc.e_commerce.dto.UserUpdateDTO;
import com.genc.e_commerce.entity.User;
import com.genc.e_commerce.repository.UserRepository;
import com.genc.e_commerce.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
 class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User mockUser;

    @BeforeEach
     void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new User();
        mockUser.setUserId(1L);
        mockUser.setUsername("testuser");
        mockUser.setPassword("password");
        mockUser.setEmail("test@example.com");
    }

    // Test adding a new user when the username does not already exist.
    @Test
     void testAddUser_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        User savedUser = userService.addUser(mockUser);

        assertNotNull(savedUser);
        assertEquals("testuser", savedUser.getUsername());
        verify(userRepository).save(any(User.class));
    }

    // Test adding a new user when the username already exists.
    @Test
     void testAddUser_UserAlreadyExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.addUser(mockUser);
        });

        assertEquals("User with username 'testuser' already exists!", exception.getMessage());
    }

    // Test successful login with correct username and password.
    @Test
     void testLoginUser_Success() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        // Encode password to match
        String encodedPassword = new BCryptPasswordEncoder().encode("password");
        mockUser.setPassword(encodedPassword);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

        User loggedInUser = userService.loginUser(loginRequest);

        assertNotNull(loggedInUser);
        assertEquals("testuser", loggedInUser.getUsername());
    }

    // Test failed login with incorrect username or password.
    @Test
     void testLoginUser_Failure() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("wronguser");
        loginRequest.setPassword("wrongpass");

        when(userRepository.findByUsername("wronguser")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            userService.loginUser(loginRequest);
        });
    }

    // Test fetching user profile by user ID.
    @Test
     void testGetUserProfile() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        Optional<User> userProfile = userService.getUserProfile(1L);

        assertTrue(userProfile.isPresent());
        assertEquals("testuser", userProfile.get().getUsername());
    }

    // Test updating user profile information.
    @Test
     void testUpdateUserProfile() {
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setUsername("updatedUser");
        updateDTO.setEmail("updated@example.com");
        updateDTO.setPassword("newpassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        User updatedUser = userService.updateUserProfile(1L, updateDTO);

        assertNotNull(updatedUser);
        assertEquals("updatedUser", updatedUser.getUsername());
        assertEquals("updated@example.com", updatedUser.getEmail());
    }

    // Test updating user profile when user ID does not exist.
    @Test
     void testUpdateUserProfile_UserNotFound() {
       UserUpdateDTO updateDTO = new UserUpdateDTO();
       updateDTO.setUsername("newuser");
       updateDTO.setEmail("newemail@example.com");
       updateDTO.setPassword("newpassword");

       when(userRepository.findById(999L)).thenReturn(Optional.empty());

       User result = userService.updateUserProfile(999L, updateDTO);

       assertNull(result);
       verify(userRepository, never()).save(any(User.class));
    }
}