package com.mholodniuk.searchthedocs.management.room.dto;

import com.mholodniuk.searchthedocs.management.access.dto.AccessKeyResponse;
import com.mholodniuk.searchthedocs.management.user.dto.UserDto;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Builder
public record RoomResponse(
        Long id,
        String name,
        boolean isPrivate,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt,
        List<String> tags,
        UserDto owner,
        Collection<AccessKeyResponse> accessKeys
) {
}
