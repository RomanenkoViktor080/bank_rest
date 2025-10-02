package com.example.bankcards.policy.card_request;

import com.example.bankcards.entity.card.CardRequest;
import com.example.bankcards.entity.card.CardRequestStatus;
import com.example.bankcards.exception.api.ForbiddenException;
import org.springframework.stereotype.Component;

@Component
public class ProceedCardRequestPolicyImpl implements ProceedCardRequestPolicy {
    @Override
    public void validate(CardRequest cardRequest) {
        if (!cardRequest.getStatus().equals(CardRequestStatus.PENDING)) {
            throw new ForbiddenException("Request cannot be updated because it is no longer pending");
        }
    }
}
