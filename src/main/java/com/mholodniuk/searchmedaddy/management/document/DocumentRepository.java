package com.mholodniuk.searchmedaddy.management.document;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, String> {
    @Query("""
            select d from Document d
            left join fetch d.room
            left join fetch d.owner
            left join fetch d.fileLocation
            """)
    List<Document> findAllWithAll();
}