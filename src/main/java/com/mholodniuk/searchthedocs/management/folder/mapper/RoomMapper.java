package com.mholodniuk.searchthedocs.management.folder.mapper;

import com.mholodniuk.searchthedocs.management.folder.Room;
import com.mholodniuk.searchthedocs.management.folder.dto.RoomCreatedResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoomMapper {
    public static RoomCreatedResponse toCreatedResponse(Room room) {
        return RoomCreatedResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .isPrivate(room.getIsPrivate())
                .createdAt(room.getCreatedAt())
                .modifiedAt(room.getModifiedAt())
                .build();
    }
}
