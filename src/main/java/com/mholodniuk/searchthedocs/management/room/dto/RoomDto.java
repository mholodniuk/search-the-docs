package com.mholodniuk.searchthedocs.management.room.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record RoomDto(
        Long id,
        String name,
        boolean isPrivate,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {
}
