package com.mholodniuk.searchthedocs.management.room;

import com.mholodniuk.searchthedocs.common.validation.ErrorMessage;
import com.mholodniuk.searchthedocs.management.access.AccessService;
import com.mholodniuk.searchthedocs.management.exception.InvalidResourceUpdateException;
import com.mholodniuk.searchthedocs.management.exception.ResourceNotFoundException;
import com.mholodniuk.searchthedocs.management.room.dto.*;
import com.mholodniuk.searchthedocs.management.room.mapper.RoomMapper;
import com.mholodniuk.searchthedocs.management.user.User;
import com.mholodniuk.searchthedocs.management.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.mholodniuk.searchthedocs.common.operation.Operation.applyIfChanged;
import static com.mholodniuk.searchthedocs.common.utils.CommonUtils.concatenate;
import static com.mholodniuk.searchthedocs.common.utils.CommonUtils.toUUID;
import static com.mholodniuk.searchthedocs.management.room.RoomConsts.DEFAULT_ROOM_NAME;


@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final AccessService accessService;

    public RoomDto createRoom(CreateRoomRequest createRoomRequest) {
        if (roomRepository.existsByNameAndOwnerId(createRoomRequest.name(), createRoomRequest.ownerId())) {
            throw new InvalidResourceUpdateException("Cannot create entity", new ErrorMessage("name", "This user already owns room with this name", createRoomRequest.name()));
        }

        var owner = userRepository.findById(createRoomRequest.ownerId())
                .orElseThrow(() -> new ResourceNotFoundException("No user with id %s found".formatted(createRoomRequest.ownerId())));

        var room = RoomMapper.fromRequest(createRoomRequest);
        room.setCreatedAt(LocalDateTime.now());
        room.setModifiedAt(LocalDateTime.now());
        room.setOwner(owner);
        roomRepository.save(room);

        accessService.createSelfAccessKey(owner, room);

        return RoomMapper.toDTO(room);
    }

    public RoomDto createDefaultRoom(User user) {
        var room = new Room();
        room.setOwner(user);
        room.setCreatedAt(LocalDateTime.now());
        room.setModifiedAt(LocalDateTime.now());
        room.setName(DEFAULT_ROOM_NAME);
        room.setPrivate(true);
        roomRepository.save(room);
        accessService.createSelfAccessKey(user, room);

        return RoomMapper.toDTO(room);
    }

    public RoomDto updateRoom(Long roomId, UpdateRoomRequest updateRequest) {
        var room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("No room with id %s found".formatted(roomId)));

        applyIfChanged(room.getName(), updateRequest.name(), (updated) -> {
            if (roomRepository.existsByNameAndOwnerId(updateRequest.name(), room.getOwner().getId())) {
                throw new InvalidResourceUpdateException("Cannot update entity", new ErrorMessage("name", "This user already owns room with this name", updateRequest.name()));
            } else {
                room.setName(updated);
            }
        });
        applyIfChanged(room.isPrivate(), updateRequest.isPrivate(), room::setPrivate);
        room.setModifiedAt(LocalDateTime.now());

        var updated = roomRepository.save(room);

        return RoomMapper.toDTO(updated);
    }

    public List<RoomDto> findAllRooms() {
        return roomRepository.findAll().stream()
                .map(RoomMapper::toDTO)
                .toList();
    }

    public Optional<RoomResponse> findRoomById(Long roomId) {
        return roomRepository.findByIdWithOwner(roomId).map(RoomMapper::toResponse);
    }

    public List<String> findTagsInRoom(Long roomId) {
        return roomRepository.findAllTagsByRoom(roomId);
    }

    public List<ExtendedRoomDto> findRoomsByOwnerId(Long userId) {
        return roomRepository.findAllByOwnerId(userId);
    }

    public List<ExtendedRoomDto> findAvailableRooms(Long userId) {
        var ownedRooms = findRoomsByOwnerId(userId).stream()
                .sorted((r1, r2) -> Long.compare(r2.documentCount(), r1.documentCount())).toList();

        var accessKeys = accessService.findUserReceivedAccessKeys(userId)
                .stream()
                .map(accessKey -> toUUID(accessKey.id()))
                .collect(Collectors.toSet());

        var invitedRooms = roomRepository.findAllByAccessKeys(accessKeys);

        return concatenate(ownedRooms, invitedRooms);
    }

    public void deleteById(Long roomId) {
        var room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("No room with id %s found".formatted(roomId)));
        // todo: delete all documents from this room
        roomRepository.delete(room);
    }
}
