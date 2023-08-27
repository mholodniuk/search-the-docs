package com.mholodniuk.searchthedocs.management.customer.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CustomerCreatedResponse(
        Long id,
        String username,
        String displayName,
        String email,
        LocalDateTime createdAt,
        String token
) {
}
