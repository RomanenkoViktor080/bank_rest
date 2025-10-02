package com.example.bankcards.controller.user.card_request;

import com.example.bankcards.dto.card_request.BlockCardRequestDto;
import com.example.bankcards.dto.card_request.CardRequestDto;
import com.example.bankcards.service.card_request.CardRequestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Tag(
        name = "User Card Requests",
        description = "APIs for users to create and manage requests related to their own bank cards, "
                      + "such as blocking, unblocking, replacement, or other actions"
)
@PreAuthorize("hasAuthority('USER')")
@RequestMapping("/api/v1/requests")
public class CardRequestController {
    private final CardRequestService cardRequestService;

    @PostMapping("/block")
    public ResponseEntity<CardRequestDto> transfer(
            @RequestBody @Valid BlockCardRequestDto dto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cardRequestService.block(dto));
    }
}
