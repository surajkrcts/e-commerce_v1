package com.genc.e_commerce.dto;

import com.genc.e_commerce.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {

    private Long userId;
    private String username;
    private User.Role role;


}
