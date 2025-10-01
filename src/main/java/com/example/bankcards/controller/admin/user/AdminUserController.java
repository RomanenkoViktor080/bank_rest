package com.example.bankcards.controller.admin.user;

import com.example.bankcards.dto.user.UserDto;
import com.example.bankcards.dto.user.UserFilterDto;
import com.example.bankcards.service.user.UserService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/api/v1/admin/users")
@Tag(name = "User management", description = "Endpoints for managing users by administrators")
public class AdminUserController {
    private final UserService service;

    @Operation(
            summary = "Get paginated and filtered list of users",
            description = "Retrieve a paginated list of users with optional filtering"
    )
    @GetMapping
    public ResponseEntity<Page<UserDto>> get(
            @ParameterObject @Valid UserFilterDto dto,
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity
                .ok()
                .body(service.get(dto, pageable));
    }


    @PutMapping("/{id}/ban")
    public ResponseEntity<UserDto> ban(
            @PathVariable UUID id
    ) {
        return ResponseEntity
                .ok()
                .body(service.ban(id));
    }
}
