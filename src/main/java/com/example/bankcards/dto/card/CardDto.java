package com.example.bankcards.dto.card;

import com.example.bankcards.entity.card.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Schema(description = "Information about a user card")
public record CardDto(
        @Schema(description = "Unique identifier of the card", example = "2e0919f1-957b-4db4-b42e-424a35dc18e2")
        UUID id,

        @Schema(description = "Masked PAN of the card for security", example = "**** **** **** 1234")
        String maskedPan,

        @Schema(description = "Current status of the card")
        CardStatus status,

        @Schema(description = "Current balance of the card", example = "1000.50")
        BigDecimal balance,

        @Schema(description = "First name of the card owner (snapshot at card creation)", example = "John")
        String firstNameSnapshot,

        @Schema(description = "Last name of the card owner", example = "Doe")
        String lastNameSnapshot,

        @Schema(description = "Expiry month of the card", example = "12")
        Integer expiryMonth,

        @Schema(description = "Expiry year of the card", example = "34")
        Integer expiryYear,

        @Schema(description = "Timestamp when the card was created")
        LocalDateTime createdAt
) {
}
