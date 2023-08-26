package com.mholodniuk.searchthedocs.management.access;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccessService {
    private final AccessKeyRepository accessKeyRepository;

    public boolean hasAccess(Long userId, Long roomId, Authentication authentication) {
        if (authentication != null) log.info(authentication.toString());
        return accessKeyRepository.existsAccessKeyByParticipantIdAndRoomId(userId, roomId);
    }
}
