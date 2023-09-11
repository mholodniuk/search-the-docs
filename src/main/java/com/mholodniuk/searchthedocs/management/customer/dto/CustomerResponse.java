package com.mholodniuk.searchthedocs.management.customer.dto;

import com.mholodniuk.searchthedocs.management.room.dto.RoomDTO;
import lombok.Builder;

import java.util.List;

@Builder
public record CustomerResponse(
        Long id,
        String username,
        String displayName,
        String email,
        List<RoomDTO> rooms
) {
}
