package com.mholodniuk.searchthedocs.management.customer.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateCustomerRequest(
        @NotBlank
        String username,
        @NotBlank
        String displayName,
        @Email
        String email,
        @NotBlank
        String password) {
}
