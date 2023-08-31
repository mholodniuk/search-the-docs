package com.mholodniuk.searchthedocs.management.document.dto;

import com.mholodniuk.searchthedocs.management.customer.dto.CustomerDTO;
import com.mholodniuk.searchthedocs.management.room.dto.RoomDTO;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record DocumentResponse(
        UUID id,
        String name,
        List<String> tags,
        String contentType,
        LocalDateTime uploadedAt,
        String filePath,
        String storage,
        RoomDTO room,
        CustomerDTO owner
) {
}
