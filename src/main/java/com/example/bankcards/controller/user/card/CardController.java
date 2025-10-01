package com.example.bankcards.controller.user.card;

import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.dto.card.UserCardFilterDto;
import com.example.bankcards.service.card.CardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Tag(
        name = "User Card Management",
        description = "APIs for regular users to view and manage their own bank cards. Supports listing cards"
)
@PreAuthorize("hasAuthority('USER')")
@RequestMapping("/api/v1/cards")
public class CardController {
    private final CardService cardService;

    @GetMapping
    public ResponseEntity<Page<CardDto>> get(
            @ParameterObject @Valid UserCardFilterDto dto,
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity
                .ok()
                .body(cardService.get(dto, pageable));
    }
}
