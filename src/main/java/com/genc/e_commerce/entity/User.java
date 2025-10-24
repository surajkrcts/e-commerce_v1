package com.genc.e_commerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    @Column(unique = true)
    private String username;
    @Column(nullable = false, length = 60)
    private String password;
    @NotBlank(message = "email should not be null")
    @Email(message = "email should be valid")
    private String email;
    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role {
        CUSTOMER, ADMIN
    }


}
