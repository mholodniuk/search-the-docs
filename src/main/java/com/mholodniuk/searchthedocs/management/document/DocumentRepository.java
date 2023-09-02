package com.mholodniuk.searchthedocs.management.document;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {
    @Query("""
            select d from Document d
            left join fetch d.room
            left join fetch d.owner
            left join fetch d.fileLocation
            where d.id = :id
            """)
    Optional<Document> findByIdWithExtraInfo(UUID id);


    boolean existsByNameAndRoomId(String name, Long roomId);
}