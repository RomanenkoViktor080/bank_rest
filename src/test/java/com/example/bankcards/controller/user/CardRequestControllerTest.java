package com.example.bankcards.controller.user;

import com.example.bankcards.dto.card_request.BlockCardRequestDto;
import com.example.bankcards.dto.card_request.CardRequestDto;
import com.example.bankcards.service.card_request.CardRequestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CardRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CardRequestService cardRequestService;

    @Test
    @DisplayName("Test transfer endpoint")
    @WithMockUser(authorities = "USER")
    public void transfer() throws Exception {
        CardRequestDto response = CardRequestDto.builder()
                .id(UUID.randomUUID())
                .build();
        BlockCardRequestDto requestDto = BlockCardRequestDto.builder()
                .cardId(UUID.randomUUID())
                .idempotencyKey("TEST")
                .build();

        when(cardRequestService.block(requestDto)).thenReturn(response);
        mockMvc.perform(post("/api/v1/requests/block")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf())
                )
                .andExpect(status().isCreated());
    }
}
