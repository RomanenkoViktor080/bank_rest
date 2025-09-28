package com.example.bankcards.repository;

import com.example.bankcards.entity.role.Role;
import com.example.bankcards.entity.role.RoleType;
import com.example.bankcards.exception.api.EntityNotFoundException;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Long> {
    Optional<Role> getByType(RoleType type);

    default Role getByTypeOrThrow(RoleType type) {
        return getByType(type).orElseThrow(() -> new EntityNotFoundException("Role not found, type: " + type));
    }
}