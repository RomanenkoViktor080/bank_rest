package com.example.bankcards.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDateTime;

@Schema(description = "Contains the access token and its expiration information")
@Builder
public record ResponseToken(
        @Schema(
                description = "JWT token used for authorization",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        )
        @NotBlank
        String value,

        @Schema(
                description = "Date and time when the token will expire (ISO-8601 format)",
                example = "2025-07-15T12:34:56"
        )
        @NotBlank
        LocalDateTime expireAt
) {
}
