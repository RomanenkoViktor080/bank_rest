package com.example.bankcards.dto.transaction;

import java.math.BigDecimal;

public record DepositRequestDto(
        BigDecimal amount,
        String idempotencyKey
) {
}
