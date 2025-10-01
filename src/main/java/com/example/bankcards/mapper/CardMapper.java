package com.example.bankcards.mapper;

import com.example.bankcards.dto.card.CreateCardDto;
import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.entity.card.Card;
import com.example.bankcards.util.masker.PanMasker;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE,
        uses = PanMasker.class
)
public interface CardMapper {
    Card toCard(CreateCardDto dto, String panHash, String panLast4);

    @Named("toCardDto")
    @Mapping(source = "panLast4", target = "maskedPan", qualifiedByName = "maskPan")
    CardDto toCardDto(Card entity);
}