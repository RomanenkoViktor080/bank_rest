package com.example.bankcards.dto.card;

import com.example.bankcards.entity.card.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Filter criteria for retrieving the authenticated user's cards")
public record CardFilterDto(
        @Schema(
                description = "Status of the card to filter by"
        )
        CardStatus status
) {
}
