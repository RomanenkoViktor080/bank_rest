package com.example.bankcards.controller.admin;

import com.example.bankcards.dto.card.AdminCardFilterDto;
import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.dto.card.CreateCardDto;
import com.example.bankcards.service.card.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminCardControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CardService cardService;

    @Test
    @DisplayName("Test create card")
    @WithMockUser(authorities = "ADMIN")
    public void create() throws Exception {
        CreateCardDto request = CreateCardDto.builder()
                .userId(UUID.randomUUID())
                .pan("4242424242424242")
                .firstNameSnapshot("TEST")
                .lastNameSnapshot("TEST")
                .expiryYear(2027)
                .expiryMonth(12)
                .build();
        CardDto response = CardDto.builder()
                .id(UUID.randomUUID())
                .balance(BigDecimal.TEN)
                .build();
        when(cardService.create(request)).thenReturn(response);
        mockMvc.perform(post("/api/v1/admin/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf())
                )
                .andExpect(status().isCreated());
    }


    @Test
    @DisplayName("Test get users cards list")
    @WithMockUser(authorities = "ADMIN")
    public void testGet() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        AdminCardFilterDto dto = mock(AdminCardFilterDto.class);
        Page<CardDto> page = new PageImpl<>(List.of(), pageable, 2);

        when(cardService.get(any(AdminCardFilterDto.class), any(Pageable.class))).thenReturn(page);
        mockMvc.perform(get("/api/v1/admin/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }
}
