package com.mholodniuk.searchthedocs.management.room.mapper;

import com.mholodniuk.searchthedocs.management.access.mapper.AccessKeyMapper;
import com.mholodniuk.searchthedocs.management.room.Room;
import com.mholodniuk.searchthedocs.management.room.dto.CreateRoomRequest;
import com.mholodniuk.searchthedocs.management.room.dto.RoomDto;
import com.mholodniuk.searchthedocs.management.room.dto.RoomResponse;
import com.mholodniuk.searchthedocs.management.user.mapper.UserMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoomMapper {
    public static Room fromRequest(CreateRoomRequest createRoomRequest) {
        var room = new Room();
        room.setName(createRoomRequest.name());
        room.setPrivate(createRoomRequest.isPrivate());
        return room;
    }

    public static RoomDto toDTO(Room room) {
        return RoomDto.builder()
                .id(room.getId())
                .name(room.getName())
                .isPrivate(room.isPrivate())
                .createdAt(room.getCreatedAt())
                .modifiedAt(room.getModifiedAt())
                .build();
    }

    public static RoomResponse toResponse(Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .isPrivate(room.isPrivate())
                .createdAt(room.getCreatedAt())
                .modifiedAt(room.getModifiedAt())
                .owner(UserMapper.toDTO(room.getOwner()))
                .accessKeys(AccessKeyMapper.toResponse(room.getAccessKeys()))
                .build();
    }

    public static RoomResponse appendTags(RoomResponse room, List<String> tags) {
        return RoomResponse.builder()
                .id(room.id())
                .name(room.name())
                .isPrivate(room.isPrivate())
                .createdAt(room.createdAt())
                .modifiedAt(room.modifiedAt())
                .owner(room.owner())
                .accessKeys(room.accessKeys())
                .tags(tags)
                .build();
    }
}
