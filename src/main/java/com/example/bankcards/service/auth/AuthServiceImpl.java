package com.example.bankcards.service.auth;

import com.example.bankcards.dto.auth.JwtTokens;
import com.example.bankcards.dto.auth.SignInRequest;
import com.example.bankcards.dto.auth.SignUpRequest;
import com.example.bankcards.dto.auth.Token;
import com.example.bankcards.entity.RefreshToken;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.role.Role;
import com.example.bankcards.entity.role.RoleType;
import com.example.bankcards.exception.api.ForbiddenException;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.RefreshTokenRepository;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public JwtTokens signUp(SignUpRequest dto) {
        Role role = roleRepository.getByTypeOrThrow(RoleType.USER);
        User user = userMapper.toUser(dto);

        user.setRole(role);
        user.setPassword(passwordEncoder.encode(dto.password()));

        userRepository.save(user);
        Token refreshToken = jwtService.generateRefreshToken(user);
        saveRefreshToken(refreshToken, user);

        logAuthAction("Регистрация пользователя", user);
        Token accessToken = jwtService.generateAccessToken(user);

        return JwtTokens.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    @Transactional
    public JwtTokens signIn(SignInRequest dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.username(), dto.password())
        );
        User user = userRepository.findByUsername(dto.username())
                .orElseThrow();
        Token accessToken = jwtService.generateAccessToken(user);
        Token refreshToken = jwtService.generateRefreshToken(user);
        saveRefreshToken(refreshToken, user);
        logAuthAction("Аутентификация пользователя", user);
        return JwtTokens.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void saveRefreshToken(Token token, User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .expiredAt(token.expireAt())
                .token(token.value())
                .isRevoked(false)
                .user(user)
                .build();
        refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional
    public Token refreshToken(String refreshToken) {
        RefreshToken refreshTokenEntity = refreshTokenRepository.getValidToken(refreshToken)
                .orElseThrow(() -> new ForbiddenException("Can't get new access value"));
        User user = refreshTokenEntity.getUser();
        LocalDateTime expiredAt = LocalDateTime.now()
                .plus(jwtService.getRefreshSecretExpiration(), ChronoUnit.MILLIS);

        refreshTokenEntity.setExpiredAt(expiredAt);
        refreshTokenRepository.save(refreshTokenEntity);

        logAuthAction("Генерация access токена через refresh токена", user);
        return jwtService.generateAccessToken(user);
    }

    private void logAuthAction(String msg, User user) {
        log.info("action: {}. Id: {}, username: {}", msg, user.getId(), user.getUsername());
    }
}
