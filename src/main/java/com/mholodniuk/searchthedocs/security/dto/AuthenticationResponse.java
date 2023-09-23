package com.mholodniuk.searchthedocs.security.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
public record AuthenticationResponse(
        String token,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        Long id
) {
}

