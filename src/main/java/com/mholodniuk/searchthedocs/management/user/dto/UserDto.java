package com.mholodniuk.searchthedocs.management.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserDto(
        Long id,
        String username,
        String displayName,
        String email,
        String token
) {
}
