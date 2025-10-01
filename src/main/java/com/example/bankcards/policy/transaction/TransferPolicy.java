package com.example.bankcards.policy.transaction;

import com.example.bankcards.entity.card.Card;

import java.math.BigDecimal;
import java.util.UUID;

public interface TransferPolicy {
    void validate(Card fromCard, Card toCard, BigDecimal amount, UUID userId);
}
