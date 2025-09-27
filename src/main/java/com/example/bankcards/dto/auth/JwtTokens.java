package com.example.bankcards.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record JwtTokens(
        @NotBlank
        Token accessToken,

        @NotBlank
        Token refreshToken
) {
}
