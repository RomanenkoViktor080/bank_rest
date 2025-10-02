package com.example.bankcards.security.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.bankcards.dto.auth.Token;

import java.util.Map;
import java.util.function.Function;

public interface JwtService {
    int getRefreshSecretExpiration();

    String extractUsername(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimResolver);

    boolean isTokenValid(String token, UserDetails userDetails);

    Token generateAccessToken(UserDetails userDetails);

    Token generateAccessToken(Map<String, Object> extraClaims, UserDetails userDetails);

    Token generateRefreshToken(UserDetails userDetails);
}
