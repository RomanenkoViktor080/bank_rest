package com.example.bankcards.controller.user;

import com.example.bankcards.controller.user.auth.AuthController;
import com.example.bankcards.dto.auth.JwtTokens;
import com.example.bankcards.dto.auth.SignInRequest;
import com.example.bankcards.dto.auth.SignUpRequest;
import com.example.bankcards.dto.auth.Token;
import com.example.bankcards.service.auth.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    private static final Token TOKEN = new Token("test", LocalDateTime.now(), System.currentTimeMillis());
    private static final JwtTokens TOKENS = JwtTokens.builder().accessToken(TOKEN).refreshToken(TOKEN).build();
    private static final String REFRESH_TOKEN = "test";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @Test
    @DisplayName("User registration")
    public void testUserRegister() throws Exception {
        SignUpRequest req = new SignUpRequest("john", "test");
        when(authService.signUp(req)).thenReturn(TOKENS);
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(cookie().exists(AuthController.REFRESH_COOKIE_NAME))
                .andExpect(cookie().httpOnly(AuthController.REFRESH_COOKIE_NAME, true))
                .andExpect(cookie().secure(AuthController.REFRESH_COOKIE_NAME, true));
    }

    @Test
    @DisplayName("User login")
    public void testUserAuthentication() throws Exception {
        SignInRequest req = new SignInRequest("john", "test");
        when(authService.signIn(req)).thenReturn(TOKENS);
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                )
                .andExpect(status().isOk())

                .andExpect(cookie().exists(AuthController.REFRESH_COOKIE_NAME))
                .andExpect(cookie().httpOnly(AuthController.REFRESH_COOKIE_NAME, true))
                .andExpect(cookie().secure(AuthController.REFRESH_COOKIE_NAME, true));
    }

    @Test
    @DisplayName("Refresh token")
    public void refreshToken() throws Exception {
        when(authService.refreshToken(REFRESH_TOKEN)).thenReturn(TOKEN);
        mockMvc.perform(post("/api/v1/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(AuthController.REFRESH_COOKIE_NAME, REFRESH_TOKEN))
                )
                .andExpect(status().isOk());
    }
}
