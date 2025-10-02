package com.example.bankcards.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload for user authentication")
public record SignInRequest(
        @Schema(
                description = "Username of the user attempting to authenticate",
                example = "john"
        )
        @NotBlank(message = "Username is required")
        String username,

        @Schema(
                description = "Password of the user",
                example = "P@ssw0rd123"
        )
        @NotBlank(message = "Password is required")
        String password
) {
}
