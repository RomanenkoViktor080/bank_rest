package com.example.bankcards.dto.card;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

import java.util.UUID;

@Builder
@Schema(description = "Request payload for creating a new card (admin only)")
public record CreateCardDto(
        @Schema(description = "ID of the user who will own the card")
        @NotNull(message = "User is required")
        UUID userId,

        @Schema(description = "Primary Account Number (PAN) of the card", example = "4242424242424242")
        @Length(min = 13, max = 16, message = "PAN must be between 13 and 16 characters")
        String pan,

        @Schema(description = "First name of the card owner (snapshot at creation)", example = "John")
        @NotNull(message = "First name is required")
        String firstNameSnapshot,

        @Schema(description = "Last name of the card owner (snapshot at creation)", example = "Doe")
        @NotNull(message = "Last name is required")
        String lastNameSnapshot,

        @Schema(description = "Expiry month of the card (1-12)", example = "12")
        @NotNull(message = "Expiry month is required")
        @Min(value = 1, message = "Expiry month must be between 1 and 12")
        @Max(value = 12, message = "Expiry month must be between 1 and 12")
        Integer expiryMonth,

        @Schema(description = "Expiry year of the card", example = "34")
        @NotNull(message = "Expiry year is required")
        Integer expiryYear
) {
}
