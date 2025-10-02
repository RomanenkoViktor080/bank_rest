package com.example.bankcards.controller.admin.card_request;

import com.example.bankcards.dto.card_request.CardRequestDto;
import com.example.bankcards.dto.card_request.FilterCardRequestDto;
import com.example.bankcards.dto.card_request.ProcessCardRequestDto;
import com.example.bankcards.service.card_request.CardRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@Tag(
        name = "Admin Card Requests",
        description = "APIs for administrators to view and process user requests "
                      + "related to bank cards, such as blocking, unblocking, or other actions"
)
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/api/v1/admin/requests")
public class AdminCardRequestController {
    private final CardRequestService cardRequestService;

    @Operation(
            summary = "Get all card requests",
            description = "Retrieve a paginated list of all card requests"
    )
    @GetMapping
    public ResponseEntity<Page<CardRequestDto>> get(
            @ParameterObject @Valid FilterCardRequestDto dto,
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity
                .ok()
                .body(cardRequestService.get(dto, pageable));
    }

    @Operation(
            summary = "Process a user card request",
            description = "Approve or reject a specific card request submitted by a user"
    )
    @PutMapping("/{id}")
    public ResponseEntity<CardRequestDto> process(
            @PathVariable UUID id,
            @RequestBody @Valid ProcessCardRequestDto dto
    ) {
        return ResponseEntity
                .ok()
                .body(cardRequestService.process(id, dto));
    }
}
