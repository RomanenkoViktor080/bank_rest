package com.example.bankcards.policy.transaction;

import com.example.bankcards.entity.card.Card;
import com.example.bankcards.entity.card.CardStatus;
import com.example.bankcards.exception.api.DataValidationException;
import com.example.bankcards.exception.api.ForbiddenException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class TransferPolicyImpl implements TransferPolicy {
    @Override
    public void validate(Card fromCard, Card toCard, BigDecimal amount, UUID userId) {
        if (!fromCard.getUser().getId().equals(userId) || !toCard.getUser().getId().equals(userId)) {
            throw new ForbiddenException(
                    "Card does not belong to the authenticated user",
                    String.format(
                            "Card does not belong to the authenticated user, "
                            + "fromCardId: %s, toCardId: %s, authUserId: %s",
                            fromCard.getUser().getId(),
                            toCard.getUser().getId(),
                            userId
                    )
            );
        }

        if (fromCard.getId().equals(toCard.getId())) {
            throw new DataValidationException("Cannot transfer to the same card");
        }

        if (fromCard.getStatus() != CardStatus.ACTIVE || toCard.getStatus() != CardStatus.ACTIVE) {
            throw new DataValidationException("Both cards must be active");
        }

        if (fromCard.getBalance().compareTo(amount) < 0) {
            throw new DataValidationException(
                    "Insufficient funds",
                    String.format("Insufficient funds, cardId: %s, balance: %s, amount: %s",
                            fromCard.getId(), fromCard.getBalance(), amount)
            );
        }
    }
}
