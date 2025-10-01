package com.example.bankcards.repository;

import com.example.bankcards.entity.card.CardRequest;
import com.example.bankcards.exception.api.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface CardRequestRepository extends JpaRepository<CardRequest, UUID>, JpaSpecificationExecutor<CardRequest> {
    default CardRequest findByIdOrThrow(UUID id) {
        return findById(id).orElseThrow(() -> new EntityNotFoundException(
                "CardRequest not found",
                String.format("CardRequest not found, id: %s", id)
        ));
    }
}
