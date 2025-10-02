package com.example.bankcards.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload for user registration")
public record SignUpRequest(
        @Schema(
                description = "Desired username for the new user",
                example = "john"
        )
        @NotBlank(message = "Username is required")
        String username,

        @Schema(
                description = "Password for the new user",
                example = "P@ssw0rd123"
        )
        @NotBlank(message = "Password is required")
        String password
) {
}
