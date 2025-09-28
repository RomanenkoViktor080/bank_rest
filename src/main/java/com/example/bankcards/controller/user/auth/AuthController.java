package com.example.bankcards.controller.user.auth;

import com.example.bankcards.dto.auth.JwtTokens;
import com.example.bankcards.dto.auth.ResponseToken;
import com.example.bankcards.dto.auth.SignInRequest;
import com.example.bankcards.dto.auth.SignUpRequest;
import com.example.bankcards.dto.auth.Token;
import com.example.bankcards.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(
        name = "Authentication",
        description = "Endpoints for user registration, login, token refresh"
)
public class AuthController {
    public static final String REFRESH_COOKIE_NAME = "refresh_token";

    private final AuthService authService;

    @Operation(
            summary = "User registration",
            description = "Creates a new user and returns an access token",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User successfully registered. " +
                                          "Returns an access token and sets an HttpOnly cookie with the refresh token"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Invalid input data"
                    )
            }
    )
    @PostMapping("/register")
    public ResponseEntity<ResponseToken> signUp(
            @RequestBody @Valid SignUpRequest dto
    ) {
        JwtTokens jwtTokens = authService.signUp(dto);
        return createAuthenticationResponse(jwtTokens);
    }

    @Operation(
            summary = "User authentication",
            description = "Validates username and password and returns an access token",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful authentication. " +
                                          "Returns an access token and sets an HttpOnly cookie with the refresh token"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Invalid credentials"
                    )
            }
    )
    @PostMapping("/login")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseToken> signIn(
            @RequestBody @Valid SignInRequest dto
    ) {
        JwtTokens jwtTokens = authService.signIn(dto);
        return createAuthenticationResponse(jwtTokens);
    }

    @Operation(
            summary = "Obtain a new access token",
            description = "Returns a new access token based on the refresh token stored in an HttpOnly cookie",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "A new access token and its expiration time"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Refresh token not found"
                    )
            }
    )
    @PostMapping("/refresh-token")
    public ResponseEntity<ResponseToken> refreshToken(
            @CookieValue(value = REFRESH_COOKIE_NAME) String refreshToken
    ) {
        Token token = authService.refreshToken(refreshToken);
        return ResponseEntity.ok()
                .body(new ResponseToken(
                        token.value(),
                        token.expireAt()
                ));
    }

    private ResponseEntity<ResponseToken> createAuthenticationResponse(JwtTokens jwtTokens) {
        ResponseCookie refreshCookie = ResponseCookie.from(
                        REFRESH_COOKIE_NAME,
                        jwtTokens.refreshToken().value()
                )
                .httpOnly(true)
                .secure(true)
                .maxAge(Duration.ofMillis(jwtTokens.refreshToken().expiration()))
                .sameSite("Strict")
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new ResponseToken(
                        jwtTokens.accessToken().value(),
                        jwtTokens.accessToken().expireAt())
                );
    }
}
