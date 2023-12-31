package com.mholodniuk.searchthedocs.management.room.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateRoomRequest(
        @NotBlank
        String name,
        @NotNull
        Boolean isPrivate,
        @NotNull
        Long ownerId) {
}