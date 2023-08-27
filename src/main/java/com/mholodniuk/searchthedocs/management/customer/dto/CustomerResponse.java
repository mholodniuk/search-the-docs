package com.mholodniuk.searchthedocs.management.customer.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CustomerResponse(
        Long id,
        String username,
        String displayName,
        String email,
        LocalDateTime createdAt,
        String token
) {
}
