package com.mholodniuk.searchthedocs.management.user.dto;

import lombok.Builder;

@Builder
public record UserResponse(
        Long id,
        String username,
        String displayName,
        String email
) {
}
