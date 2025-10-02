package com.example.bankcards.dto.transaction;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record TransactionResponseDto(
        BigDecimal amount,
        LocalDateTime createdAt
) {
}
