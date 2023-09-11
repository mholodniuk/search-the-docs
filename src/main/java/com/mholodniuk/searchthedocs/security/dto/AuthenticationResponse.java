package com.mholodniuk.searchthedocs.security.dto;

import lombok.Builder;

@Builder
public record AuthenticationResponse(
        String token
) {
}

