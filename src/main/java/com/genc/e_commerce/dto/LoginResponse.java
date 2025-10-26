package com.genc.e_commerce.dto;

import com.genc.e_commerce.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data Transfer Object (DTO) used to send a confirmation response back to the client
 * after a successful login. It contains essential, non-sensitive user information.
 */
// @Data is a Lombok annotation that automatically generates boilerplate code
// such as getters, setters, toString(), equals(), and hashCode().
@Data
// @AllArgsConstructor is a Lombok annotation that generates a constructor
// with a parameter for every field in the class.
@AllArgsConstructor
public class LoginResponse {

    // The unique identifier of the logged-in user.
    private Long userId;

    // The username of the logged-in user.
    private String username;

    // The role of the logged-in user (e.g., ADMIN, CUSTOMER), used for authorization on the client side.
    private User.Role role;

}