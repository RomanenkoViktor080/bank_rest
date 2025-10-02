package com.example.bankcards.dto.card_request;

import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.entity.card.CardRequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Schema(description = "Information about a card request")
public record CardRequestDto(
        @Schema(description = "Unique identifier of the card request")
        UUID id,

        @Schema(description = "Current status of the request")
        CardRequestStatus status,

        @Schema(description = "Information about the card associated with this request")
        CardDto card,

        @Schema(description = "Timestamp when the request was created")
        LocalDateTime createdAt,

        @Schema(description = "Timestamp when the request was last updated")
        LocalDateTime updatedAt
) {
}
