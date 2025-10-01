package com.example.bankcards.dto.user;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record UserDto(
        UUID id,
        String username,
        boolean isBanned,
        LocalDateTime createdAt
) {
}
