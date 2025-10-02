package com.example.bankcards.mapper;

import com.example.bankcards.dto.transaction.TransactionResponseDto;
import com.example.bankcards.entity.card.CardTransaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface TransactionMapper {
    TransactionResponseDto toTransactionResponseDtoResponse(CardTransaction entity);
}