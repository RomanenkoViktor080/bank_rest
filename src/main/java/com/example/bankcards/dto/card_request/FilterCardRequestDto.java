package com.example.bankcards.dto.card_request;

import com.example.bankcards.entity.card.CardRequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Filter criteria for retrieving card requests")
public record FilterCardRequestDto(
        @Schema(description = "Status of the card request to filter by")
        CardRequestStatus status
) {
}
