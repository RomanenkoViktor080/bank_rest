package com.example.bankcards.service;

import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.dto.transaction.TransferRequestDto;
import com.example.bankcards.dto.transaction.TransferResponseDto;
import com.example.bankcards.entity.card.Card;
import com.example.bankcards.entity.card.CardStatus;
import com.example.bankcards.entity.card.CardTransaction;
import com.example.bankcards.entity.card.CardTransactionType;
import com.example.bankcards.exception.api.ConflictException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.mapper.TransactionMapper;
import com.example.bankcards.policy.transaction.TransferPolicy;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.CardTransactionRepository;
import com.example.bankcards.service.transaction.TransactionServiceImpl;
import com.example.bankcards.util.auth.AuthUserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String IDEMPOTENCY_KEY = "TEST";
    private static final BigDecimal AMOUNT = BigDecimal.valueOf(200);
    private static final BigDecimal FROM_CARD_BALANCE = BigDecimal.valueOf(100);
    private static final BigDecimal TO_CARD_BALANCE = BigDecimal.valueOf(50);

    @InjectMocks
    private TransactionServiceImpl service;

    @Mock
    private CardTransactionRepository cardTransactionRepository;
    @Mock
    private TransferPolicy transferPolicy;
    @Mock
    private CardRepository cardRepository;
    @Mock
    private CardMapper cardMapper;
    @Mock
    private TransactionMapper transactionMapper;
    @Mock
    private AuthUserContext authUserContext;

    private UUID fromCardId;
    private UUID toCardId;
    private Card fromCard;
    private Card toCard;

    @BeforeEach
    void init() {
        fromCardId = UUID.randomUUID();
        toCardId = UUID.randomUUID();

        fromCard = new Card();
        fromCard.setId(fromCardId);
        fromCard.setBalance(FROM_CARD_BALANCE);
        fromCard.setStatus(CardStatus.ACTIVE);

        toCard = new Card();
        toCard.setId(toCardId);
        toCard.setBalance(TO_CARD_BALANCE);
        toCard.setStatus(CardStatus.ACTIVE);
    }

    @DisplayName("transfer: create new transaction and update balances when no existing transaction")
    @Test
    void transfer_createsTransactionAndUpdatesBalances_whenNoExistingTransaction() {
        TransferRequestDto dto = new TransferRequestDto(fromCardId, toCardId, AMOUNT, IDEMPOTENCY_KEY);

        CardDto fromDto = new CardDto(fromCardId, "**** **** **** 1111", CardStatus.ACTIVE,
                BigDecimal.valueOf(80), "First", "Last", 12, 2028, LocalDateTime.now());
        CardDto toDto = new CardDto(toCardId, "**** **** **** 2222", CardStatus.ACTIVE,
                BigDecimal.valueOf(70), "First", "Last", 6, 2027, LocalDateTime.now());

        when(cardRepository.findByIdForUpdateOrThrow(fromCardId)).thenReturn(fromCard);
        when(cardRepository.findByIdForUpdateOrThrow(toCardId)).thenReturn(toCard);
        when(cardTransactionRepository.getByIdempotencyKey(IDEMPOTENCY_KEY)).thenReturn(Optional.empty());
        when(authUserContext.getUserId()).thenReturn(USER_ID);
        when(cardMapper.toCardDto(fromCard)).thenReturn(fromDto);
        when(cardMapper.toCardDto(toCard)).thenReturn(toDto);
        doNothing().when(transferPolicy).validate(fromCard, toCard, AMOUNT, USER_ID);

        TransferResponseDto response = service.transfer(dto);

        verify(transferPolicy, times(1)).validate(fromCard, toCard, AMOUNT, USER_ID);
        verify(cardTransactionRepository, times(1)).save(any(CardTransaction.class));
        assertEquals(FROM_CARD_BALANCE.subtract(AMOUNT), fromCard.getBalance());
        assertEquals(TO_CARD_BALANCE.add(AMOUNT), toCard.getBalance());
        assertEquals(fromDto, response.fromCard());
        assertEquals(toDto, response.toCard());
    }

    @DisplayName("transfer: return existing transaction when idempotency transaction exists and matches")
    @Test
    void transfer_returnsExisting_whenTransactionMatches() {
        TransferRequestDto dto = new TransferRequestDto(fromCardId, toCardId, AMOUNT, IDEMPOTENCY_KEY);
        CardDto fromDto = new CardDto(fromCardId, "**** **** **** 1111", CardStatus.ACTIVE, FROM_CARD_BALANCE,
                "First", "Last", 12, 2028, LocalDateTime.now());
        CardDto toDto = new CardDto(toCardId, "**** **** **** 2222", CardStatus.ACTIVE, TO_CARD_BALANCE,
                "First", "Last", 6, 2027, LocalDateTime.now());

        CardTransaction existing = new CardTransaction();
        existing.setId(UUID.randomUUID());
        existing.setAmount(AMOUNT);
        existing.setCard(fromCard);
        existing.setRelatedCard(toCard);
        existing.setIdempotencyKey(IDEMPOTENCY_KEY);
        existing.setType(CardTransactionType.TRANSFER);
        existing.setCreatedAt(LocalDateTime.now().minusMinutes(1));

        when(cardRepository.findByIdForUpdateOrThrow(fromCardId)).thenReturn(fromCard);
        when(cardRepository.findByIdForUpdateOrThrow(toCardId)).thenReturn(toCard);
        when(cardTransactionRepository.getByIdempotencyKey(IDEMPOTENCY_KEY)).thenReturn(Optional.of(existing));
        when(cardMapper.toCardDto(fromCard)).thenReturn(fromDto);
        when(cardMapper.toCardDto(toCard)).thenReturn(toDto);

        TransferResponseDto response = service.transfer(dto);

        verify(transferPolicy, never()).validate(any(), any(), any(), any());
        verify(cardTransactionRepository, never()).save(any());
        assertEquals(FROM_CARD_BALANCE, fromCard.getBalance());
        assertEquals(TO_CARD_BALANCE, toCard.getBalance());
        assertEquals(existing.getCreatedAt(), response.dateTime());
        assertEquals(fromDto, response.fromCard());
        assertEquals(toDto, response.toCard());
    }

    @DisplayName("test conflict when existing transaction has different params")
    @Test
    void testConflictWhenExistingTransactionHasDifferentParams() {
        BigDecimal existingAmount = BigDecimal.valueOf(10);

        TransferRequestDto dto = new TransferRequestDto(fromCardId, toCardId, AMOUNT, IDEMPOTENCY_KEY);

        CardTransaction existing = new CardTransaction();
        existing.setId(UUID.randomUUID());
        existing.setAmount(existingAmount);
        existing.setCard(fromCard);
        existing.setRelatedCard(toCard);
        existing.setIdempotencyKey(IDEMPOTENCY_KEY);
        existing.setType(CardTransactionType.TRANSFER);
        existing.setCreatedAt(LocalDateTime.now());

        when(cardRepository.findByIdForUpdateOrThrow(fromCardId)).thenReturn(fromCard);
        when(cardRepository.findByIdForUpdateOrThrow(toCardId)).thenReturn(toCard);
        when(cardTransactionRepository.getByIdempotencyKey(IDEMPOTENCY_KEY)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> service.transfer(dto))
                .isInstanceOf(ConflictException.class);

        assertEquals(FROM_CARD_BALANCE, fromCard.getBalance());
        assertEquals(TO_CARD_BALANCE, toCard.getBalance());
        verify(cardTransactionRepository, never()).save(any());
    }
}
