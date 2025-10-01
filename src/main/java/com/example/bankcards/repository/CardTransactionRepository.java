package com.example.bankcards.repository;

import com.example.bankcards.entity.card.CardTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CardTransactionRepository extends JpaRepository<CardTransaction, UUID> {
    Optional<CardTransaction> getByIdempotencyKey(String idempotencyKey);
}
