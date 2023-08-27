package com.mholodniuk.searchthedocs.management.folder;

import com.mholodniuk.searchthedocs.management.customer.Customer;
import com.mholodniuk.searchthedocs.management.folder.dto.RoomCreatedResponse;
import com.mholodniuk.searchthedocs.management.folder.mapper.RoomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.mholodniuk.searchthedocs.management.folder.RoomConsts.DEFAULT_ROOM_NAME;


@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;

    public RoomCreatedResponse createDefaultRoom(Customer customer) {
        var room = new Room();
        room.setOwner(customer);
        room.setCreatedAt(LocalDateTime.now());
        room.setModifiedAt(LocalDateTime.now());
        room.setName(DEFAULT_ROOM_NAME);
        room.setIsPrivate(true);
        roomRepository.save(room);

        return RoomMapper.toCreatedResponse(room);
    }
}
