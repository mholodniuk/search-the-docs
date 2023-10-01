package com.mholodniuk.searchthedocs.management.access;

import com.mholodniuk.searchthedocs.security.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccessValidationService {
    private final AccessKeyRepository accessKeyRepository;

    public boolean validateUserAccess(Authentication authentication, Long userId) {
        var principal = (User) authentication.getPrincipal();
        return principal.getId().equals(userId);
    }

    public boolean validateRoomOwner(Authentication authentication, Long roomId) {
        return validateRoomAccessRight(authentication, roomId, AccessRight.OWNER);
    }

    public boolean validateRoomReadAccess(Authentication authentication, Long roomId) {
        return validateRoomAccessRight(authentication, roomId, AccessRight.VIEW);
    }

    public boolean validateRoomFullAccess(Authentication authentication, Long roomId) {
        return validateRoomAccessRight(authentication, roomId, AccessRight.FULL);
    }

    public boolean validateRoomAccessRight(Authentication authentication, Long roomId, AccessRight accessRight) {
        var principal = (User) authentication.getPrincipal();
        var access = checkAccessByRoomId(principal.getId(), roomId);
        return access == accessRight;
    }

    public boolean validateDocumentAccess(Authentication authentication, String documentId) {
        var principal = (User) authentication.getPrincipal();
        return checkAccessByDocumentId(principal.getId(), UUID.fromString(documentId)) != AccessRight.NONE;
    }

    public AccessRight checkAccessByDocumentId(Long participantId, UUID documentId) {
        return accessKeyRepository
                .findAccessRightsByParticipantIdAndDocumentIdOnDate(participantId, documentId, LocalDateTime.now())
                .orElse(AccessRight.NONE);
    }

    public AccessRight checkAccessByRoomId(Long participantId, Long roomId) {
        return accessKeyRepository
                .findAccessRightsByParticipantIdAndRoomIdOnDate(participantId, roomId, LocalDateTime.now())
                .orElse(AccessRight.NONE);
    }
}
