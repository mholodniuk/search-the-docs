package com.mholodniuk.searchthedocs.management.user.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(
        @NotBlank
        String username,
        @NotBlank
        String displayName,
        @Email
        String email,
        @NotBlank
        String password) {
}
