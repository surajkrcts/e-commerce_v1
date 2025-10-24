package com.genc.e_commerce.service;

import com.genc.e_commerce.dto.LoginRequest;
import com.genc.e_commerce.entity.User;
import com.genc.e_commerce.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    PasswordEncoder passwordEncoder;

    public UserService() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }


    public User addUser(User user) {
        String encodedPasswod = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPasswod);
        Optional<User> existingUser = this.userRepository.findByUsername(user.getUsername());

        if (existingUser.isPresent()) {

            throw new RuntimeException("User with username '" + user.getUsername() + "' already exists!");
        }
        return userRepository.save(user);
    }

        public User loginUser(LoginRequest loginRequest) throws RuntimeException{
        String username=loginRequest.getUsername();
        String password=loginRequest.getPassword();
            Optional<User> userDetails = userRepository.findByUsername(username);
            if (userDetails.isPresent()) {
                User user = userDetails.get();
                String storedEncodedPassword = user.getPassword();

                if (passwordEncoder.matches(password, storedEncodedPassword)) {
                    return userDetails.get();
                }
            }
            throw new RuntimeException("not found");
        }

    public Optional<User> getUserProfile(Long userId) {
        Optional<User> userDetails = userRepository.findById(userId);
        if (userDetails.isPresent()) {
            User user = userDetails.get();
            String storedEncodedPassword = user.getPassword();
            if(passwordEncoder.matches(user.getPassword(), storedEncodedPassword)) ;
            return userDetails;
        }
        return Optional.empty();

    }
    public User updateUserProfile(Long userId, User user){
      Optional<User> userDetailsFromDB=userRepository.findById(userId);
      if(userDetailsFromDB.isPresent()){
          User existingUser=userDetailsFromDB.get();

          existingUser.setUsername(user.getUsername());
          existingUser.setEmail(user.getEmail());
          existingUser.setRole(user.getRole());

          return userRepository.save(existingUser);
      }
      return null;
    }
}
