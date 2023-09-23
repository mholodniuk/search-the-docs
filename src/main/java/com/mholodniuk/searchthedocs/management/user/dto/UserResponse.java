package com.mholodniuk.searchthedocs.management.user.dto;

import com.mholodniuk.searchthedocs.management.room.dto.RoomDto;
import lombok.Builder;

import java.util.List;

@Builder
public record UserResponse(
        Long id,
        String username,
        String displayName,
        String email
) {
}
