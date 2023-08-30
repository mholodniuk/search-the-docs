package com.mholodniuk.searchthedocs.management.room;

import com.mholodniuk.searchthedocs.management.room.dto.RoomResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("""
                select new com.mholodniuk.searchthedocs.management.room.dto.RoomResponse(r.id, r.name, r.isPrivate, r.createdAt, r.modifiedAt)
                from Room r where r.owner.id = :customerId
            """)
    List<RoomResponse> findAllByOwnerId(Long customerId);

    boolean existsByNameAndOwnerId(String name, Long ownerId);
}