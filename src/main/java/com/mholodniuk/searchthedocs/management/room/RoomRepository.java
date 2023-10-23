package com.mholodniuk.searchthedocs.management.room;

import com.mholodniuk.searchthedocs.management.room.dto.ExtendedRoomDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("""
                select new com.mholodniuk.searchthedocs.management.room.dto.ExtendedRoomDto(
                    r.id, r.name, r.isPrivate, true, r.createdAt, r.modifiedAt,
                    (select count(*) from Document d where d.room.id = r.id))
                from Room r where r.owner.id = :userId
            """)
    List<ExtendedRoomDto> findAllByOwnerId(Long userId);

    @Query("""
                select new com.mholodniuk.searchthedocs.management.room.dto.ExtendedRoomDto(
                    r.id, r.name, r.isPrivate, false, r.createdAt, r.modifiedAt,
                    (select count(*) from Document d where d.room.id = r.id))
                from Room r
                join AccessKey a on a.room.id = r.id
                where a.id in :accessKeys
            """)
    List<ExtendedRoomDto> findAllByAccessKeys(Set<UUID> accessKeys);

    @Query("""
                select r from Room r left join fetch r.documents where r.id = :roomId
            """)
    Optional<Room> findByIdWithDocuments(Long roomId);

    @Query("""
                select r from Room r left join fetch r.owner where r.id = :roomId
            """)
    Optional<Room> findByIdWithOwner(Long roomId);

    boolean existsByNameAndOwnerId(String name, Long ownerId);

    @Query("select r from Room r where r.id = :id and r.isPrivate = false")
    Optional<Room> findPublicRoomById(Long id);

    @Query(value = "select distinct unnest(d.tags) t from documents d where d.room_id = :roomId order by t", nativeQuery = true)
    List<String> findAllTagsByRoom(Long roomId);
}