package com.mholodniuk.searchthedocs.management.folder.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record RoomResponse(
        Long id,
        String name,
        boolean isPrivate,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt) {
}
