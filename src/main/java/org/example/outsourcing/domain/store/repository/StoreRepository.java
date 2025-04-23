package org.example.outsourcing.domain.store.repository;

import org.example.outsourcing.domain.store.entity.Store;
import org.example.outsourcing.domain.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {

    long countByOwner(User owner);

    @EntityGraph(attributePaths = {"owner"})
    List<Store> findByNameContaining(String keyword);
}
