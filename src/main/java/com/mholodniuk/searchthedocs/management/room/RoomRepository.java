package com.mholodniuk.searchthedocs.management.room;

import com.mholodniuk.searchthedocs.management.room.dto.ExtendedRoomDto;
import com.mholodniuk.searchthedocs.management.room.dto.RoomDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("""
                select new com.mholodniuk.searchthedocs.management.room.dto.ExtendedRoomDto(
                    r.id, r.name, r.isPrivate, r.createdAt, r.modifiedAt,
                    (select count(*) from Document d where d.room.id = r.id))
                from Room r where r.owner.id = :userId
            """)
    List<ExtendedRoomDto> findAllByOwnerId(Long userId);

    @Query("""
                select r from Room r left join fetch r.documents where r.id = :roomId
            """)
    Optional<Room> findByIdWithDocuments(Long roomId);

    boolean existsByNameAndOwnerId(String name, Long ownerId);

    @Query("select r from Room r where r.id = :id and r.isPrivate = false")
    Optional<Room> findPublicRoomById(Long id);
}