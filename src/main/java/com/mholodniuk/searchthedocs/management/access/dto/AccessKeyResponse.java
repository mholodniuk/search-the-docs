package com.mholodniuk.searchthedocs.management.access.dto;

import com.mholodniuk.searchthedocs.management.access.AccessRight;
import com.mholodniuk.searchthedocs.management.room.dto.RoomDTO;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AccessKeyResponse(
        String id,
        String name,
        RoomDTO room,
        String recipient,
        LocalDateTime validTo,
        AccessRight accessRight
) {
}
