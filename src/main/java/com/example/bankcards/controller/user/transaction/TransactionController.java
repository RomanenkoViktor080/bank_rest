package com.example.bankcards.controller.user.transaction;

import com.example.bankcards.dto.transaction.DepositRequestDto;
import com.example.bankcards.dto.transaction.TransactionResponseDto;
import com.example.bankcards.dto.transaction.TransferRequestDto;
import com.example.bankcards.dto.transaction.TransferResponseDto;
import com.example.bankcards.service.transaction.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@Tag(
        name = "User Transactions",
        description = "APIs for users to perform financial operations such as transfers between their own cards"
)
@PreAuthorize("hasAuthority('USER')")
@RequestMapping("/api/v1/transactions")
public class TransactionController {
    private final TransactionService cardService;

    @Operation(
            summary = "Transfer funds between user's cards",
            description = "Perform a transfer between two cards owned by the authenticated user. "
                          + "Both the source and target cards must belong to the user, be active. "
    )
    @PostMapping("/transfer")
    public ResponseEntity<TransferResponseDto> transfer(
            @RequestBody @Valid TransferRequestDto dto
    ) {
        return ResponseEntity
                .ok()
                .body(cardService.transfer(dto));
    }

    @Operation(
            summary = "Deposit funds to a card (test only)",
            description = "Add funds to a user's card. This endpoint is intended for testing and demo purposes only. "
                          + "The card must belong to the authenticated user and must be active."
    )
    @PostMapping("/deposit/{cardId}")
    public ResponseEntity<TransactionResponseDto> deposit(
            @PathVariable UUID cardId,
            @RequestBody @Valid DepositRequestDto dto
    ) {
        return ResponseEntity
                .ok()
                .body(cardService.deposit(cardId, dto));
    }
}
