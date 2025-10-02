package com.example.bankcards.dto.transaction;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record TransferRequestDto(
        UUID fromCardId,
        UUID toCardId,
        BigDecimal amount,
        String idempotencyKey
) {
}
