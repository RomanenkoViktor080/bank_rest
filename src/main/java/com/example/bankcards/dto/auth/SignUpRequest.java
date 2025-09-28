package com.example.bankcards.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record SignUpRequest(
        @NotBlank(message = "Username")
        String username,

        @NotBlank(message = "Password")
        String password
) {
}
