package com.example.bankcards.controller.admin;

import com.example.bankcards.dto.card_request.CardRequestDto;
import com.example.bankcards.dto.card_request.FilterCardRequestDto;
import com.example.bankcards.dto.card_request.ProcessCardRequestDto;
import com.example.bankcards.service.card_request.CardRequestService;
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
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class AdminCardRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CardRequestService cardRequestService;

    @Test
    @DisplayName("Test get users cards requests list")
    @WithMockUser(authorities = "ADMIN")
    public void TestGet() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        FilterCardRequestDto dto = mock(FilterCardRequestDto.class);
        Page<CardRequestDto> page = new PageImpl<>(List.of(), pageable, 2);

        when(cardRequestService.get(any(FilterCardRequestDto.class), any(Pageable.class))).thenReturn(page);
        mockMvc.perform(get("/api/v1/admin/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @PutMapping("/{id}")
    public void testProcessCardRequest() throws Exception {
        UUID id = UUID.randomUUID();
        ProcessCardRequestDto dto = ProcessCardRequestDto.builder()
                .status(ProcessCardRequestDto.CardRequestProceedStatus.APPROVE)
                .build();
        CardRequestDto response = CardRequestDto.builder()
                .id(id)
                .build();

        when(cardRequestService.process(id, dto)).thenReturn(response);
        mockMvc.perform(put("/api/v1/admin/requests/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }
}
