package com.example.bankcards.service.transaction;

import com.example.bankcards.dto.transaction.DepositRequestDto;
import com.example.bankcards.dto.transaction.TransactionResponseDto;
import com.example.bankcards.dto.transaction.TransferRequestDto;
import com.example.bankcards.dto.transaction.TransferResponseDto;
import com.example.bankcards.entity.card.Card;
import com.example.bankcards.entity.card.CardTransaction;
import com.example.bankcards.entity.card.CardTransactionType;
import com.example.bankcards.exception.api.ConflictException;
import com.example.bankcards.exception.api.ForbiddenException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.mapper.TransactionMapper;
import com.example.bankcards.policy.transaction.TransferPolicy;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.CardTransactionRepository;
import com.example.bankcards.util.auth.AuthUserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {
    private final CardTransactionRepository cardTransactionRepository;
    private final TransferPolicy transferPolicy;
    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final TransactionMapper transactionMapper;
    private final AuthUserContext authUserContext;

    @Transactional
    @Override
    public TransferResponseDto transfer(TransferRequestDto dto) {
        Card fromCard = cardRepository.findByIdForUpdateOrThrow(dto.fromCardId());
        Card toCard = cardRepository.findByIdForUpdateOrThrow(dto.toCardId());
        CardTransaction transaction = cardTransactionRepository
                .getByIdempotencyKey(dto.idempotencyKey())
                .orElseGet(() -> {
                    UUID userId = authUserContext.getUserId();
                    transferPolicy.validate(fromCard, toCard, dto.amount(), userId);
                    fromCard.setBalance(fromCard.getBalance().subtract(dto.amount()));
                    toCard.setBalance(toCard.getBalance().add(dto.amount()));

                    CardTransaction result = CardTransaction.builder()
                            .amount(dto.amount())
                            .card(fromCard)
                            .relatedCard(toCard)
                            .idempotencyKey(dto.idempotencyKey())
                            .type(CardTransactionType.TRANSFER)
                            .build();
                    cardTransactionRepository.save(result);

                    return result;
                });

        checkTransaction(transaction, dto.amount(), dto.fromCardId(), dto.toCardId(), dto.idempotencyKey());

        return TransferResponseDto.builder()
                .fromCard(cardMapper.toCardDto(fromCard))
                .toCard(cardMapper.toCardDto(toCard))
                .dateTime(transaction.getCreatedAt())
                .build();
    }

    /**
     * Пополнение баланса карты.
     * <p>
     * ВАЖНО: данный эндпоинт реализован исключительно для целей тестирования.
     * В банковской системе пополнение выполняется через платёжного провайдера
     * или процессинговую систему, а не напрямую через API
     */
    @Transactional
    @Override
    public TransactionResponseDto deposit(UUID cardId, DepositRequestDto dto) {
        UUID userId = authUserContext.getUserId();
        Card card = cardRepository.findByIdForUpdateOrThrow(cardId);
        if (!card.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You don't have access to card");
        }
        card.setBalance(card.getBalance().add(dto.amount()));
        cardRepository.save(card);
        CardTransaction transaction = cardTransactionRepository
                .getByIdempotencyKey(dto.idempotencyKey())
                .orElseGet(() -> {
                            CardTransaction result = CardTransaction
                                    .builder()
                                    .amount(dto.amount())
                                    .card(card)
                                    .idempotencyKey(dto.idempotencyKey())
                                    .type(CardTransactionType.DEPOSIT)
                                    .build();
                            cardTransactionRepository.save(result);
                            return result;
                        }
                );
        checkTransaction(transaction, dto.amount(), cardId, null, dto.idempotencyKey());
        return transactionMapper.toTransactionResponseDtoResponse(transaction);
    }

    private void checkTransaction(
            CardTransaction transaction,
            BigDecimal amount,
            UUID fromCardId,
            UUID toCardId,
            String idempotencyKey
    ) {
        boolean amountMatches = transaction.getAmount() != null
                                && amount != null
                                && transaction.getAmount().compareTo(amount) == 0;

        UUID existsFrom = transaction.getCard() != null ? transaction.getCard().getId() : null;
        UUID existsRelated = transaction.getRelatedCard() != null ? transaction.getRelatedCard().getId() : null;

        boolean idsMatch = Objects.equals(existsFrom, fromCardId) && Objects.equals(existsRelated, toCardId);
        boolean keyMatches = Objects.equals(transaction.getIdempotencyKey(), idempotencyKey);

        if (!amountMatches || !idsMatch || !keyMatches) {
            throw new ConflictException(
                    "Transaction with this idempotency key already exists with different parameters"
            );
        }
    }

}
