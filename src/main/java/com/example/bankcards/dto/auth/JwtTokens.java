package com.example.bankcards.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
@Schema(description = "JWT tokens response containing access and refresh tokens")
public record JwtTokens(
        @NotBlank
        @Schema(
                description = "Access token used for authenticating API requests. " +
                              "Short-lived and must be included in the Authorization header as 'Bearer <token>'",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        )
        Token accessToken,

        @Schema(
                description = "Refresh token used to obtain a new access token when the current one expires. "
                              + "Long-lived and should be stored securely",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        )
        @NotBlank
        Token refreshToken
) {
}
