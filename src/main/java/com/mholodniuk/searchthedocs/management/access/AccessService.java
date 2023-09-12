package com.mholodniuk.searchthedocs.management.access;

import com.mholodniuk.searchthedocs.management.access.dto.AccessKeyResponse;
import com.mholodniuk.searchthedocs.management.access.dto.GrantAccessRequest;
import com.mholodniuk.searchthedocs.management.access.mapper.AccessKeyMapper;
import com.mholodniuk.searchthedocs.management.user.User;
import com.mholodniuk.searchthedocs.management.user.UserRepository;
import com.mholodniuk.searchthedocs.management.exception.InvalidResourceCreationException;
import com.mholodniuk.searchthedocs.management.exception.InvalidResourceDeletionException;
import com.mholodniuk.searchthedocs.management.exception.ResourceNotFoundException;
import com.mholodniuk.searchthedocs.management.room.Room;
import com.mholodniuk.searchthedocs.management.room.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccessService {
    private final AccessKeyRepository accessKeyRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public AccessKeyResponse grantAccess(Long roomId, GrantAccessRequest grantAccessRequest) {
        // todo: check if user already has access to requested room on selected date ???
        var issuedRoom = roomRepository.findPublicRoomById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("No public room with id %s found".formatted(roomId)));

        var invitedUser = userRepository.findById(grantAccessRequest.invitedId())
                .orElseThrow(() -> new ResourceNotFoundException("No user with id %s found".formatted(grantAccessRequest.invitedId())));

        if (issuedRoom.getOwner().getId().equals(invitedUser.getId())) {
            throw new InvalidResourceCreationException("Room with id %s already owned by user with id %s".formatted(roomId, grantAccessRequest.invitedId()));
        }

        var accessKey = new AccessKey();
        accessKey.setId(UUID.randomUUID());
        accessKey.setName(grantAccessRequest.keyName());
        accessKey.setValidTo(grantAccessRequest.validTo() != null
                ? grantAccessRequest.validTo().atTime(LocalTime.MAX) : null);
        accessKey.setRights(grantAccessRequest.accessRight());
        accessKey.setRoom(issuedRoom);
        accessKey.setParticipant(invitedUser);

        accessKeyRepository.save(accessKey);

        return AccessKeyMapper.toResponse(accessKey);
    }

    public void createSelfAccessKey(User invitedUser, Room issuedRoom) {
        var accessKey = new AccessKey();
        accessKey.setId(UUID.randomUUID());
        accessKey.setName("Default");
        accessKey.setValidTo(null);
        accessKey.setRights(AccessRight.ALL);
        accessKey.setRoom(issuedRoom);
        accessKey.setParticipant(invitedUser);

        accessKeyRepository.save(accessKey);
    }

    public void revokeAccess(Long roomId, Long participantId) {
        var accessKeys = accessKeyRepository.findAllByParticipantIdAndRoomId(participantId, roomId);
        if (accessKeys.isEmpty()) {
            throw new ResourceNotFoundException("No participant with id %s has access to room %s".formatted(participantId, roomId));
        }

        var issuedRoom = roomRepository.findPublicRoomById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("No public room with id %s found".formatted(roomId)));
        var invitedUser = userRepository.findById(participantId)
                .orElseThrow(() -> new ResourceNotFoundException("No user with id %s found".formatted(participantId)));

        if (issuedRoom.getOwner().getId().equals(invitedUser.getId())) {
            throw new InvalidResourceDeletionException("Cannot revoke access from owned room");
        }

        accessKeyRepository.deleteAll(accessKeys);
    }

    public List<AccessKeyResponse> findUserAccessKeys(Long userId) {
        return accessKeyRepository
                .findUserAccessKeys(userId).stream()
                .map(AccessKeyMapper::toResponse)
                .toList();
    }

    public List<AccessKeyResponse> findRoomAccessKeys(Long roomId) {
        return accessKeyRepository
                .findRoomAccessKeys(roomId).stream()
                .map(AccessKeyMapper::toResponse)
                .toList();
    }
}
