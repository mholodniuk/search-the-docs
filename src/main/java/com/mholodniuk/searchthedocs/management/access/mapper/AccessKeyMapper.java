package com.mholodniuk.searchthedocs.management.access.mapper;

import com.mholodniuk.searchthedocs.management.access.AccessKey;
import com.mholodniuk.searchthedocs.management.access.dto.AccessKeyResponse;
import com.mholodniuk.searchthedocs.management.room.mapper.RoomMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AccessKeyMapper {
    public static AccessKeyResponse toResponse(AccessKey accessKey) {
        return AccessKeyResponse.builder()
                .id(accessKey.getId().toString())
                .name(accessKey.getName())
                .accessRight(accessKey.getRights())
                .validTo(accessKey.getValidTo())
                .recipient(accessKey.getParticipant().getUsername())
                .room(RoomMapper.toDTO(accessKey.getRoom()))
                .build();
    }
}
