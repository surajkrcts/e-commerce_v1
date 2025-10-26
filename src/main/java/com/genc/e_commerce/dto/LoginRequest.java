package com.genc.e_commerce.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "username should not be null")
    private String username;
    @NotBlank(message = "password should not be null")
    private String password;

    public LoginRequest(){

    }
}
