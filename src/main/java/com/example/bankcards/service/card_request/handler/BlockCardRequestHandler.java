package com.example.bankcards.service.card_request.handler;

import com.example.bankcards.entity.User;
import com.example.bankcards.entity.card.CardRequest;
import com.example.bankcards.entity.card.CardRequestType;
import com.example.bankcards.entity.card.CardStatus;
import org.springframework.stereotype.Component;

@Component
public class BlockCardRequestHandler implements CardRequestHandler {
    @Override
    public CardRequestType getType() {
        return CardRequestType.BLOCK;
    }

    @Override
    public void onApprove(CardRequest entity, User admin) {
        entity.setUser(admin);
        entity.getCard().setStatus(CardStatus.BLOCKED);
    }
}