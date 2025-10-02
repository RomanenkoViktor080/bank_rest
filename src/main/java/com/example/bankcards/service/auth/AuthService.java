package com.example.bankcards.service.auth;


import com.example.bankcards.dto.auth.JwtTokens;
import com.example.bankcards.dto.auth.SignInRequest;
import com.example.bankcards.dto.auth.SignUpRequest;
import com.example.bankcards.dto.auth.Token;

public interface AuthService {
    JwtTokens signUp(SignUpRequest dto);

    JwtTokens signIn(SignInRequest dto);

    Token refreshToken(String refreshToken);
}
