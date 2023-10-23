package com.mholodniuk.searchthedocs.management.user.dto;

import lombok.Builder;

@Builder
public record UserDto(
        Long id,
        String username,
        String displayName,
        String email,
        String token
) {
}
