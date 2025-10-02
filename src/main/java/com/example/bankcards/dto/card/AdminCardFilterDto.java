package com.example.bankcards.dto.card;

import com.example.bankcards.entity.card.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Filter criteria for retrieving user cards (admin only)")
public record AdminCardFilterDto(
        @Schema(
                description = "Status of the card to filter by"
        )
        CardStatus status,

        @Schema(
                description = "Filter cards by owner user ID",
                example = "d290f1ee-6c54-4b01-90e6-d701748f0851"
        )
        UUID userId
) {
}
