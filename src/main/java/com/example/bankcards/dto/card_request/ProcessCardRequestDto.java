package com.example.bankcards.dto.card_request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Payload to process a user card request")
public record ProcessCardRequestDto(
        @Schema(
                description = "Decision for the card request: APPROVE to accept, REJECT to deny"
        )
        @NotNull(message = "Status is required")
        CardRequestProceedStatus status
) {
    @Schema(description = "Possible decisions for processing a card request")
    public enum CardRequestProceedStatus {
        APPROVE, REJECT
    }
}
