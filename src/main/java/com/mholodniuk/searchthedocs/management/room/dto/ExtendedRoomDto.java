package com.mholodniuk.searchthedocs.management.room.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ExtendedRoomDto(
        Long id,
        String name,
        boolean isPrivate,
        boolean isOwner,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt,
        Long documentCount
) {
}
