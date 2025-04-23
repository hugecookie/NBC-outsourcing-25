package org.example.outsourcing.domain.user.repository;

import java.util.Optional;

import org.example.outsourcing.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByEmail(String email);

	Optional<User> findByEmailAndIsDeleted(String email, boolean isDeleted);

}
