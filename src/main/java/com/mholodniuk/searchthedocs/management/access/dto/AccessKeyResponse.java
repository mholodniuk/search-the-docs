package com.mholodniuk.searchthedocs.management.access.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mholodniuk.searchthedocs.management.access.AccessRight;
import com.mholodniuk.searchthedocs.management.room.dto.RoomDto;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AccessKeyResponse(
        String id,
        String name,
        RoomDto room,
        String recipient,
        Long recipientId,
        LocalDateTime validTo,
        AccessRight accessRight
) {
}
