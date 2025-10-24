package com.genc.e_commerce.controller;

import com.genc.e_commerce.dto.LoginRequest;
import com.genc.e_commerce.dto.LoginResponse;
import com.genc.e_commerce.entity.User;
import com.genc.e_commerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/register-user")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        User userData = userService.addUser(user);
        if (userData == null) {
            response.put("data", userData);
            response.put("status", "Need to register");
        } else {
            response.put("data", userData);
            response.put("status", "User Registerd Successfully");
        }

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @PostMapping("/login-user")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        User userData = userService.loginUser(loginRequest);
        if (userData!=null) {
             LoginResponse loginResponse = new LoginResponse(
                    userData.getUserId(),
                    userData.getUsername(),
                    userData.getRole());
            return ResponseEntity.ok(loginResponse);
        }
        return new ResponseEntity<>("Invalid username and password ", HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/get-user-details/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        Optional<User> user = userService.getUserProfile(userId);
        if (user != null) {
            response.put("data", user);
            response.put("status", "All data fetched successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("data", user);
            response.put("status", "data not found");
            return new ResponseEntity<>(response, HttpStatus.FOUND);
        }
    }

    @PutMapping("/update-user-data/{userId}")
    public ResponseEntity<?> updateUserProfile(@PathVariable Long userId, @RequestBody User userData) {
        Map<String, Object> response = new HashMap<>();

        User user = userService.updateUserProfile(userId, userData);
        if (user != null) {
            response.put("data", user);
            response.put("status", "Updated successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("data", null);
            response.put("status", "not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

    }
}
