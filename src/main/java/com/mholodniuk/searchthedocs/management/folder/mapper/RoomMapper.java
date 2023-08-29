package com.mholodniuk.searchthedocs.management.folder.mapper;

import com.mholodniuk.searchthedocs.management.folder.Room;
import com.mholodniuk.searchthedocs.management.folder.dto.CreateRoomRequest;
import com.mholodniuk.searchthedocs.management.folder.dto.RoomResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoomMapper {
    public static RoomResponse toResponse(Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .isPrivate(room.getIsPrivate())
                .createdAt(room.getCreatedAt())
                .modifiedAt(room.getModifiedAt())
                .build();
    }

    public static Room fromRequest(CreateRoomRequest createRoomRequest) {
        var room = new Room();
        room.setName(createRoomRequest.name());
        room.setIsPrivate(createRoomRequest.isPrivate());
        return room;
    }
}
