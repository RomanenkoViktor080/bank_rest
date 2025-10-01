package com.example.bankcards.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Filter parameters for searching and retrieving users")
public record UserFilterDto(
        @Schema(
                description = "Search keyword to filter users ( by username, email, or name. Only username for now)",
                example = "john"
        )
        String search
) {
}
