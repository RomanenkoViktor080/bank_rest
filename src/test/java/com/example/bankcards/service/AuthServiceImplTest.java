package com.example.bankcards.service;

import com.example.bankcards.dto.auth.SignInRequest;
import com.example.bankcards.dto.auth.SignUpRequest;
import com.example.bankcards.dto.auth.Token;
import com.example.bankcards.entity.RefreshToken;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.role.Role;
import com.example.bankcards.entity.role.RoleType;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.RefreshTokenRepository;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.service.JwtService;
import com.example.bankcards.service.auth.AuthServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {
    private static final String EXISTING_TOKEN = "valid-refresh-token";
    private static final String NEW_ACCESS_TOKEN = "new-access-token";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final Role ROLE = new Role(1L, RoleType.USER);


    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;
    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Captor
    private ArgumentCaptor<User> userCaptor;
    @Captor
    private ArgumentCaptor<UsernamePasswordAuthenticationToken> authTokenCaptor;

    @Test
    @DisplayName("User registration")
    public void testUserRegister() {
        String encodedPassword = "encoded";
        when(passwordEncoder.encode(any())).thenReturn(encodedPassword);
        when(jwtService.generateAccessToken(any())).thenReturn(new Token(null, null, 1L));
        when(jwtService.generateRefreshToken(any())).thenReturn(new Token(null, null, 1L));
        when(roleRepository.getByTypeOrThrow(ROLE.getType())).thenReturn(ROLE);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(USER_ID);
            return u;
        });
        SignUpRequest signUpRequest = new SignUpRequest("john", "pass");

        authService.signUp(signUpRequest);

        verify(userMapper).toUser(signUpRequest);
        verify(passwordEncoder).encode(signUpRequest.password());
        verify(userRepository).save(userCaptor.capture());
        verify(roleRepository).getByTypeOrThrow(RoleType.USER);
        User user = userCaptor.getValue();
        assertEquals(encodedPassword, user.getPassword());
        assertEquals(ROLE, user.getRole());
        verify(jwtService).generateAccessToken(any());
        verify(jwtService).generateRefreshToken(any());
        verify(refreshTokenRepository).save(any());
    }

    @Test
    @DisplayName("User sign in")
    public void testUserAuthentication() {
        SignInRequest request = new SignInRequest("test", "password");
        when(userRepository.findByUsername(request.username())).thenReturn(Optional.of(mock(User.class)));
        when(jwtService.generateAccessToken(any())).thenReturn(new Token(null, null, 1L));
        when(jwtService.generateRefreshToken(any())).thenReturn(new Token(null, null, 1L));

        authService.signIn(request);

        verify(authenticationManager).authenticate(authTokenCaptor.capture());
        UsernamePasswordAuthenticationToken passed = authTokenCaptor.getValue();
        assertEquals(request.username(), passed.getPrincipal());
        assertEquals(request.password(), passed.getCredentials());
        verify(userRepository).findByUsername(request.username());
        verify(jwtService).generateAccessToken(any());
        verify(jwtService).generateRefreshToken(any());
        verify(refreshTokenRepository).save(any());
    }

    @Test
    @DisplayName("Obtain an access token using the user's refresh token")
    public void testUserGetAccessToken() {
        User user = new User();
        user.setId(USER_ID);
        RefreshToken token = new RefreshToken(
                2L,
                user,
                NEW_ACCESS_TOKEN,
                false,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(refreshTokenRepository.getValidToken(EXISTING_TOKEN)).thenReturn(Optional.of(token));

        authService.refreshToken(EXISTING_TOKEN);

        verify(refreshTokenRepository).getValidToken(EXISTING_TOKEN);
        verify(refreshTokenRepository).save(token);
        verify(jwtService).generateAccessToken(token.getUser());
        assertEquals(user, token.getUser());
    }
}
