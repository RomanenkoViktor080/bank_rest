package com.example.bankcards.policy.card_request;

import com.example.bankcards.entity.card.CardRequest;

public interface ProceedCardRequestPolicy {
    void validate(CardRequest cardRequest);
}
