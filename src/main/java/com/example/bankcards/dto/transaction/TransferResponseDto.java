package com.example.bankcards.dto.transaction;

import com.example.bankcards.dto.card.CardDto;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TransferResponseDto(
        CardDto fromCard,

        CardDto toCard,

        LocalDateTime dateTime
) {
}
