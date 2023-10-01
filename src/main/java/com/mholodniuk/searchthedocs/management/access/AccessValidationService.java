package com.mholodniuk.searchthedocs.management.access;

import com.mholodniuk.searchthedocs.security.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Function;

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
        return validateRoomAccessRight(authentication, roomId, (access -> access == AccessRight.OWNER));
    }

    public boolean validateRoomAnyAccess(Authentication authentication, Long roomId) {
        return validateRoomAccessRight(authentication, roomId, (access -> access != AccessRight.NONE));
    }

    public boolean validateRoomReadAccess(Authentication authentication, Long roomId) {
        return validateRoomAccessRight(authentication, roomId, (access -> access == AccessRight.VIEW));
    }

    public boolean validateRoomFullAccess(Authentication authentication, Long roomId) {
        return validateRoomAccessRight(authentication, roomId, (access -> access == AccessRight.FULL));
    }

    public boolean validateRoomAccessRight(Authentication authentication, Long roomId, Function<AccessRight, Boolean> accessExpression) {
        var principal = (User) authentication.getPrincipal();
        var access = checkAccessByRoomId(principal.getId(), roomId);
        return accessExpression.apply(access);
    }

    public boolean validateDocumentAccess(Authentication authentication, String documentId) {
        var principal = (User) authentication.getPrincipal();
        return checkAccessByDocumentId(principal.getId(), UUID.fromString(documentId)) != AccessRight.NONE;
    }

    private AccessRight checkAccessByDocumentId(Long participantId, UUID documentId) {
        return accessKeyRepository
                .findAccessRightsByParticipantIdAndDocumentIdOnDate(participantId, documentId, LocalDateTime.now())
                .orElse(AccessRight.NONE);
    }

    private AccessRight checkAccessByRoomId(Long participantId, Long roomId) {
        return accessKeyRepository
                .findAccessRightsByParticipantIdAndRoomIdOnDate(participantId, roomId, LocalDateTime.now())
                .orElse(AccessRight.NONE);
    }
}
