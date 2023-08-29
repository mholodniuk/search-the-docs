package com.mholodniuk.searchthedocs.management.access;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessKeyRepository extends JpaRepository<AccessKey, String> {
    boolean existsAccessKeyByParticipantIdAndRoomId(Long participantId, Long roomId);
}