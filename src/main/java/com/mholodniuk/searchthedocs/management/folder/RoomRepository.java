package com.mholodniuk.searchthedocs.management.folder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface RoomRepository extends JpaRepository<Room, Long> {
}