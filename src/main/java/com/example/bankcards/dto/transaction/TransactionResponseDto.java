package com.example.bankcards.dto.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponseDto(
        BigDecimal amount,
        LocalDateTime createdAt
) {
}
