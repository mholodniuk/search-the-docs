package com.mholodniuk.searchthedocs.management.room;

import com.mholodniuk.searchthedocs.management.room.dto.RoomDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("""
                select new com.mholodniuk.searchthedocs.management.room.dto.RoomDTO(r.id, r.name, r.isPrivate, r.createdAt, r.modifiedAt)
                from Room r where r.owner.id = :customerId
            """)
    List<RoomDTO> findAllByOwnerId(Long customerId);

    @Query("select r from Room r join fetch r.documents where r.id = :documentId")
    Optional<Room> findByIdWithDocuments(Long documentId);

    boolean existsByNameAndOwnerId(String name, Long ownerId);
}