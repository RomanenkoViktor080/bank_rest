package com.example.bankcards.service.card;

import com.example.bankcards.dto.card.AdminCardFilterDto;
import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.dto.card.CreateCardDto;
import com.example.bankcards.dto.card.CardFilterDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CardService {
    Page<CardDto> get(AdminCardFilterDto dto, Pageable pageable);

    Page<CardDto> get(CardFilterDto dto, Pageable pageable);

    CardDto create(CreateCardDto dto);

    void delete(UUID id);
}
