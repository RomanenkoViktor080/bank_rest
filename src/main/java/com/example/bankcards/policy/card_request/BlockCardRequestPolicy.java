package com.example.bankcards.policy.card_request;

import com.example.bankcards.entity.card.Card;

import java.util.UUID;

public interface BlockCardRequestPolicy {
    void validate(Card card, UUID userId);
}
