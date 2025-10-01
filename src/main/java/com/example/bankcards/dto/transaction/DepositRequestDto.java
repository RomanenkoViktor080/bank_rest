package com.example.bankcards.dto.transaction;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record DepositRequestDto(
        BigDecimal amount,
        String idempotencyKey
) {
}
