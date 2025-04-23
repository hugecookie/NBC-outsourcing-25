package org.example.outsourcing.domain.store.repository;

import org.example.outsourcing.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
