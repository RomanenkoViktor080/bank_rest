package com.example.bankcards.service.transaction;

import com.example.bankcards.dto.transaction.DepositRequestDto;
import com.example.bankcards.dto.transaction.TransactionResponseDto;
import com.example.bankcards.dto.transaction.TransferRequestDto;
import com.example.bankcards.dto.transaction.TransferResponseDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface TransactionService {
    @Transactional
    TransferResponseDto transfer(TransferRequestDto dto);

    @Transactional
    TransactionResponseDto deposit(UUID cardId, DepositRequestDto dto);
}
