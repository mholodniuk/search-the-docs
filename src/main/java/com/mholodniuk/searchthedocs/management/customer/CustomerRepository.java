package com.mholodniuk.searchthedocs.management.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    @Query("select c from Customer c join fetch c.rooms where c.id = :customerId")
    Optional<Customer> findByIdWithRooms(Long customerId);
}