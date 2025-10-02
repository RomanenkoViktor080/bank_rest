package com.example.bankcards.mapper;

import com.example.bankcards.dto.card_request.CardRequestDto;
import com.example.bankcards.entity.card.CardRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE,
        uses = CardMapper.class
)

public interface CardRequestMapper {
    @Mapping(source = "card", target = "card", qualifiedByName = "toCardDto")
    CardRequestDto toCardRequestDto(CardRequest entity);
}