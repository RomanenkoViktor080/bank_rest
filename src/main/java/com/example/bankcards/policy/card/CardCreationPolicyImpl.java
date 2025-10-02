package com.example.bankcards.policy.card;

import com.example.bankcards.dto.card.CreateCardDto;
import com.example.bankcards.exception.api.DataValidationException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CardCreationPolicyImpl implements CardCreationPolicy {
    public void validate(CreateCardDto dto, boolean isExist) {
        validateCardExistence(isExist);
        validateCardNumber(dto.pan());
    }

    private void validateCardNumber(String pan) {
        if (!pan.matches("(?=[456]|37)[0-9]{13,16}")) {
            throw new DataValidationException("Invalid card number", "Invalid card number: " + pan);
        }
        int sum = 0;
        for (int i = pan.length() - 1, pos = 1; i >= 0; i--, pos++) {
            int digit = pan.charAt(i) - '0';
            sum += (pos % 2 == 1 ? digit : digit < 5 ? digit * 2 : digit * 2 - 9);
        }
        if (sum % 10 != 0) {
            throw new DataValidationException("Invalid card number", "Invalid card number: " + pan);
        }
    }

    private void validateCardExistence(boolean isExist) {
        if (isExist) {
            throw new DataValidationException("Card already exists");
        }
    }

    private void validateCardExpireData(CreateCardDto dto) {
        LocalDateTime now = LocalDateTime.now();
        if (
                dto.expiryYear() < now.getYear()
                || (dto.expiryYear().equals(now.getYear()) && dto.expiryMonth() > now.getMonthValue())
        ) {
            throw new DataValidationException("Card has expired");
        }
    }
}
