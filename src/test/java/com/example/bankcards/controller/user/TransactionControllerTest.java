package com.example.bankcards.controller.user;

import com.example.bankcards.dto.transaction.DepositRequestDto;
import com.example.bankcards.dto.transaction.TransactionResponseDto;
import com.example.bankcards.dto.transaction.TransferRequestDto;
import com.example.bankcards.dto.transaction.TransferResponseDto;
import com.example.bankcards.service.transaction.TransactionService;
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
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerTest {
    public static final String IDEMPOTENCY_KEY = "TEST";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransactionService cardService;

    @Test
    @DisplayName("Test transfer")
    @WithMockUser(authorities = "USER")
    public void transfer() throws Exception {
        TransferRequestDto request = TransferRequestDto.builder()
                .amount(BigDecimal.TEN)
                .fromCardId(UUID.randomUUID())
                .toCardId(UUID.randomUUID())
                .idempotencyKey(IDEMPOTENCY_KEY)
                .build();
        TransferResponseDto response = TransferResponseDto.builder()
                .dateTime(LocalDateTime.now())
                .build();
        when(cardService.transfer(request)).thenReturn(response);
        mockMvc.perform(post("/api/v1/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test deposit")
    @WithMockUser(authorities = "USER")
    @PostMapping("/deposit/{cardId}")
    public void deposit() throws Exception {
        UUID cardId = UUID.randomUUID();
        DepositRequestDto request = DepositRequestDto.builder()
                .amount(BigDecimal.TEN)
                .idempotencyKey(IDEMPOTENCY_KEY)
                .build();
        TransactionResponseDto response = TransactionResponseDto.builder()
                .amount(BigDecimal.TEN)
                .build();
        when(cardService.deposit(cardId, request)).thenReturn(response);
        mockMvc.perform(post("/api/v1/transactions/deposit/{cardId}", cardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }
}
