package com.genc.e_commerce.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Data Transfer Object (DTO) used to capture and validate user login credentials
 * sent from the client.
 */
// @Data is a Lombok annotation that automatically generates boilerplate code
// like getters, setters, toString(), equals(), and hashCode() for the fields.
@Data
public class LoginRequest {

    // The username provided by the user for login.
    // @NotBlank is a validation annotation that ensures the incoming username is not null and not just whitespace.
    @NotBlank(message = "username should not be null")
    private String username;

    // The password provided by the user for login.
    // @NotBlank ensures the incoming password is not null and not just whitespace.
    @NotBlank(message = "password should not be null")
    private String password;

    /**
     * A no-argument constructor.
     * This is required by frameworks like Jackson (for JSON deserialization) and JPA
     * to create an instance of the class before populating its fields.
     */
    public LoginRequest() {

    }
}