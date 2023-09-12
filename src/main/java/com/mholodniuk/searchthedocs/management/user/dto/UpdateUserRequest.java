package com.mholodniuk.searchthedocs.management.user.dto;


import jakarta.validation.constraints.Email;

public record UpdateUserRequest(
        String username,
        String displayName,
        @Email
        String email,
        String password) {
}

