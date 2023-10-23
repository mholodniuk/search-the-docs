package com.mholodniuk.searchthedocs.management.room.mapper;

import com.mholodniuk.searchthedocs.management.document.mapper.DocumentMapper;
import com.mholodniuk.searchthedocs.management.room.Room;
import com.mholodniuk.searchthedocs.management.room.dto.CreateRoomRequest;
import com.mholodniuk.searchthedocs.management.room.dto.RoomDto;
import com.mholodniuk.searchthedocs.management.room.dto.RoomResponse;
import com.mholodniuk.searchthedocs.management.user.mapper.UserMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;
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

    public static List<RoomDto> toDTO(Collection<Room> rooms) {
        return rooms.stream().map(RoomMapper::toDTO).toList();
    }

    public static RoomResponse toResponse(Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .isPrivate(room.isPrivate())
                .createdAt(room.getCreatedAt())
                .modifiedAt(room.getModifiedAt())
                .owner(UserMapper.toDTO(room.getOwner()))
                .build();
    }
}
