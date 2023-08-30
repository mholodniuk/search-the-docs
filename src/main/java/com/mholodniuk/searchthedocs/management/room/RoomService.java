package com.mholodniuk.searchthedocs.management.room;

import com.mholodniuk.searchthedocs.common.validation.ErrorMessage;
import com.mholodniuk.searchthedocs.management.customer.Customer;
import com.mholodniuk.searchthedocs.management.customer.CustomerRepository;
import com.mholodniuk.searchthedocs.management.exception.InvalidResourceUpdateException;
import com.mholodniuk.searchthedocs.management.exception.ResourceNotFoundException;
import com.mholodniuk.searchthedocs.management.room.dto.CreateRoomRequest;
import com.mholodniuk.searchthedocs.management.room.dto.RoomResponse;
import com.mholodniuk.searchthedocs.management.room.dto.UpdateRoomRequest;
import com.mholodniuk.searchthedocs.management.room.mapper.RoomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.mholodniuk.searchthedocs.common.operation.Operation.applyIfChanged;
import static com.mholodniuk.searchthedocs.management.room.RoomConsts.DEFAULT_ROOM_NAME;


@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final CustomerRepository customerRepository;

    public RoomResponse createRoom(CreateRoomRequest createRoomRequest) {
        if (roomRepository.existsByNameAndOwnerId(createRoomRequest.name(), createRoomRequest.ownerId())) {
            var errors = List.of(new ErrorMessage("name", "User already owns room with given name", List.of(createRoomRequest.name(), createRoomRequest.ownerId())));
            throw new InvalidResourceUpdateException("Cannot create entity", errors);
        }

        var owner = customerRepository.findById(createRoomRequest.ownerId())
                .orElseThrow(() -> new ResourceNotFoundException("No customer with id %s found".formatted(createRoomRequest.ownerId())));

        var room = RoomMapper.fromRequest(createRoomRequest);
        room.setCreatedAt(LocalDateTime.now());
        room.setModifiedAt(LocalDateTime.now());
        room.setOwner(owner);
        roomRepository.save(room);

        return RoomMapper.toResponse(room);
    }

    public RoomResponse createDefaultRoom(Customer customer) {
        var room = new Room();
        room.setOwner(customer);
        room.setCreatedAt(LocalDateTime.now());
        room.setModifiedAt(LocalDateTime.now());
        room.setName(DEFAULT_ROOM_NAME);
        room.setIsPrivate(true);
        roomRepository.save(room);

        return RoomMapper.toResponse(room);
    }

    public RoomResponse updateRoom(Long roomId, UpdateRoomRequest updateRequest) {
        var room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("No room with id %s found".formatted(roomId)));

        applyIfChanged(room.getName(), updateRequest.name(), (updated) -> {
            if (roomRepository.existsByNameAndOwnerId(updateRequest.name(), room.getOwner().getId())) {
                throw new InvalidResourceUpdateException("Cannot update entity",
                        List.of(new ErrorMessage("name", "This user already owns room named %s"
                                .formatted(updateRequest.name()), updateRequest.name())));
            } else {
                room.setName(updated);
            }
        });
        applyIfChanged(room.getIsPrivate(), updateRequest.isPrivate(), room::setIsPrivate);
        room.setModifiedAt(LocalDateTime.now());

        var updated = roomRepository.save(room);

        return RoomMapper.toResponse(updated);
    }

    public List<RoomResponse> findAllRooms() {
        return roomRepository.findAll().stream()
                .map(RoomMapper::toResponse)
                .toList();
    }

    public Optional<RoomResponse> findRoomById(Long customerId) {
        return roomRepository.findById(customerId).map(RoomMapper::toResponse);
    }

    public List<RoomResponse> findRoomsByOwnerId(Long customerId) {
        return roomRepository.findAllByOwnerId(customerId);
    }

    public void deleteById(Long roomId) {
        var room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("No room with id %s found".formatted(roomId)));
        roomRepository.delete(room);
    }
}
