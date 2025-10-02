package com.example.bankcards.repository;

import com.example.bankcards.entity.User;
import com.example.bankcards.exception.api.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    default User findByIdOrThrow(UUID uuid) {
        return findById(uuid).orElseThrow(() -> new EntityNotFoundException(
                "User not found",
                "User not found, id: " + uuid
        ));
    }
}
