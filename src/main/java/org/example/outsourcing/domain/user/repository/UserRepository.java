package org.example.outsourcing.domain.user.repository;

import java.util.Optional;

import org.example.outsourcing.domain.user.entity.Platform;
import org.example.outsourcing.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByEmailAndPlatform(String email, Platform platform);

	Optional<User> findByEmailAndPlatformAndIsDeleted(String email, Platform platform, boolean isDeleted);
}

