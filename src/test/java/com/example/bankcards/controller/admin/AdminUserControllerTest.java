package com.example.bankcards.controller.admin;

import com.example.bankcards.dto.user.UserDto;
import com.example.bankcards.dto.user.UserFilterDto;
import com.example.bankcards.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.time.LocalDateTime;
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
public class AdminUserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService service;

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void testGet() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        UserFilterDto dto = mock(UserFilterDto.class);
        Page<UserDto> page = new PageImpl<>(List.of(), pageable, 2);

        when(service.get(any(UserFilterDto.class), any(Pageable.class))).thenReturn(page);
        mockMvc.perform(get("/api/v1/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void ban() throws Exception {
        UUID id = UUID.randomUUID();
        UserDto response = UserDto.builder()
                .isBanned(true)
                .username("test")
                .createdAt(LocalDateTime.now())
                .build();

        when(service.ban(id)).thenReturn(response);
        mockMvc.perform(put("/api/v1/admin/users/{id}/ban", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }
}
