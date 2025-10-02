package com.example.bankcards.service.card_request;

import com.example.bankcards.dto.card_request.BlockCardRequestDto;
import com.example.bankcards.dto.card_request.CardRequestDto;
import com.example.bankcards.dto.card_request.FilterCardRequestDto;
import com.example.bankcards.dto.card_request.ProcessCardRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface CardRequestService {
    Page<CardRequestDto> get(FilterCardRequestDto dto, Pageable pageable);

    CardRequestDto block(BlockCardRequestDto dto);

    @Transactional
    CardRequestDto process(UUID id, ProcessCardRequestDto dto);
}
