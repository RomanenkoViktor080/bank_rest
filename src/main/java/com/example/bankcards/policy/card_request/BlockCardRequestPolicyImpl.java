package com.example.bankcards.policy.card_request;

import com.example.bankcards.entity.card.Card;
import com.example.bankcards.entity.card.CardStatus;
import com.example.bankcards.exception.api.DataValidationException;
import com.example.bankcards.exception.api.ForbiddenException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class BlockCardRequestPolicyImpl implements BlockCardRequestPolicy {
    @Override
    public void validate(Card card, UUID userId) {
        if (!card.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You can't block this card",
                    String.format(
                            "You can't block this card, cardUserId: %s, userId: %s",
                            card.getUser().getId(),
                            userId
                    )
            );
        }

        if (!card.getStatus().equals(CardStatus.ACTIVE)) {
            throw new DataValidationException("You can block only active cards");
        }
    }
}
