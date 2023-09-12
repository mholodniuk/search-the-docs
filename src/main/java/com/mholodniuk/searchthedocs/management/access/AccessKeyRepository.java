package com.mholodniuk.searchthedocs.management.access;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccessKeyRepository extends JpaRepository<AccessKey, UUID> {
    @Query("select a.rights from AccessKey a where a.room.id = :roomId and a.participant.id = :participantId and (a.validTo = null or :date < a.validTo)")
    Optional<AccessRight> findAccessRightsByParticipantIdAndRoomIdOnDate(Long participantId, Long roomId, LocalDateTime date);

    @Query("select a from AccessKey a join fetch a.participant p join fetch a.room r where p.id = :userId")
    List<AccessKey> findUserAccessKeys(Long userId);

    @Query("select a from AccessKey a join fetch a.participant p join fetch a.room r where r.id = :roomId")
    List<AccessKey> findRoomAccessKeys(Long roomId);

    List<AccessKey> findAllByParticipantIdAndRoomId(Long participantId, Long roomId);
}