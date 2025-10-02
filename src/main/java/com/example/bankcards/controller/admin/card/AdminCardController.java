package com.example.bankcards.controller.admin.card;

import com.example.bankcards.dto.card.AdminCardFilterDto;
import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.dto.card.CreateCardDto;
import com.example.bankcards.service.card.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@Tag(
        name = "Admin Card Management",
        description = "APIs for administrators to manage user bank cards. "
                      + "Includes operations for creating, blocking, activating and deleting cards"
)
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/api/v1/admin/cards")
public class AdminCardController {
    private final CardService cardService;

    @Operation(
            summary = "Create a card for a user",
            description = "Create a new bank card for a specified user"
    )
    @PostMapping
    public ResponseEntity<CardDto> create(
            @RequestBody @Valid CreateCardDto dto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cardService.create(dto));
    }

    @Operation(
            summary = "Get all user cards",
            description = "Retrieve a paginated list of all cards in the system"
    )
    @GetMapping
    public ResponseEntity<Page<CardDto>> get(
            @ParameterObject @Valid AdminCardFilterDto dto,
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity
                .ok()
                .body(cardService.get(dto, pageable));
    }


    @Operation(
            summary = "Delete a card (admin only)",
            description = "Soft-delete a card by its ID"
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public void delete(
            @PathVariable UUID id
    ) {
        cardService.delete(id);
    }
}
