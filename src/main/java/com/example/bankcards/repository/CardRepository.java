package com.example.bankcards.repository;

import com.example.bankcards.entity.card.Card;
import com.example.bankcards.exception.api.EntityNotFoundException;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID>, JpaSpecificationExecutor<Card> {
    boolean existsByPanHash(String hash);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Card c where c.id = :id")
    Optional<Card> findByIdForUpdate(@Param("id") UUID id);

    default Card findByIdForUpdateOrThrow(UUID id) {
        return findByIdForUpdate(id).orElseThrow(() -> new EntityNotFoundException(
                "Card not found",
                "Card not found, id: " + id
        ));
    }

    default Card findByIdOrThrow(UUID id) {
        return findById(id).orElseThrow(() -> new EntityNotFoundException(
                "Card not found",
                "Card not found, id: " + id
        ));
    }

}
