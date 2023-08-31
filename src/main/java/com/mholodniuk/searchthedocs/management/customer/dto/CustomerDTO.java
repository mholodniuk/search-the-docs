package com.mholodniuk.searchthedocs.management.customer.dto;

import lombok.Builder;

@Builder
public record CustomerDTO(
        Long id,
        String username,
        String displayName,
        String email,
        String token
) {
}
