package com.mholodniuk.searchthedocs.management.customer.dto;


import jakarta.validation.constraints.Email;

public record UpdateCustomerRequest(
        String username,
        String displayName,
        @Email
        String email,
        String password) {
}

