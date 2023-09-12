package com.mholodniuk.searchthedocs.management.access;

import com.mholodniuk.searchthedocs.security.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
        var principal = (User) authentication.getPrincipal();
        return checkAccess(principal.getId(), roomId) != AccessRight.NONE;
    }

    public AccessRight checkAccess(Long participantId, Long roomId) {
        return accessKeyRepository
                .findAccessRightsByParticipantIdAndRoomIdOnDate(participantId, roomId, LocalDateTime.now())
                .orElse(AccessRight.NONE);
    }
}
