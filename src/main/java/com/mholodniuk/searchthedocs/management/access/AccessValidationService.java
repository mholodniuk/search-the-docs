package com.mholodniuk.searchthedocs.management.access;

import com.mholodniuk.searchthedocs.security.model.UserEntity;
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

    public boolean validateCustomerAccess(Authentication authentication, Long customerId) {
        var principal = (UserEntity) authentication.getPrincipal();
        return principal.getId().equals(customerId);
    }

    public boolean validateRoomOwner(Authentication authentication, Long roomId) {
        var principal = (UserEntity) authentication.getPrincipal();
        return checkAccess(principal.getId(), roomId) != AccessRight.NONE;
    }

    public AccessRight checkAccess(Long participantId, Long roomId) {
        return accessKeyRepository
                .findAccessRightsByParticipantIdAndRoomIdOnDate(participantId, roomId, LocalDateTime.now())
                .orElse(AccessRight.NONE);
    }
}
