package com.mholodniuk.searchthedocs.management.customer.dto;

import lombok.Builder;

@Builder
public record CustomerResponse(
        Long id,
        String username,
        String displayName,
        String email,
        String token
) {
}
