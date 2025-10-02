package com.example.bankcards.policy.card;

import com.example.bankcards.dto.card.CreateCardDto;

public interface CardCreationPolicy {
    void validate(CreateCardDto dto, boolean isExist);
}
