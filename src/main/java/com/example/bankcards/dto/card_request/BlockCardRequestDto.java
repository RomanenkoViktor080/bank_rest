package com.example.bankcards.dto.card_request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Request payload to block a user's card")
public record BlockCardRequestDto(
        @Schema(description = "ID of the card to be blocked")
        @NotNull(message = "Card ID is required")
        UUID cardId,

        @Schema(description = "Idempotency key to ensure the request is processed only once")
        @NotNull(message = "Idempotency key is required")
        String idempotencyKey
) {
}
