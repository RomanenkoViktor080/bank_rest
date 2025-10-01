package com.example.bankcards.service.card;

import com.example.bankcards.dto.card.AdminCardFilterDto;
import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.dto.card.CreateCardDto;
import com.example.bankcards.dto.card.UserCardFilterDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CardService {
    Page<CardDto> get(AdminCardFilterDto dto, Pageable pageable);

    Page<CardDto> get(UserCardFilterDto dto, Pageable pageable);

    CardDto create(CreateCardDto dto);
}
