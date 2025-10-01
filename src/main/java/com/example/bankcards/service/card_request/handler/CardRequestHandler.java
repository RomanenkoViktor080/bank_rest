package com.example.bankcards.service.card_request.handler;

import com.example.bankcards.entity.User;
import com.example.bankcards.entity.card.CardRequest;
import com.example.bankcards.entity.card.CardRequestType;

public interface CardRequestHandler {
    CardRequestType getType();

    void onApprove(CardRequest entity, User admin);
}