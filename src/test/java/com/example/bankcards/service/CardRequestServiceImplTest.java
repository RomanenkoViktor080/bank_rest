package com.example.bankcards.service;

import com.example.bankcards.dto.card_request.BlockCardRequestDto;
import com.example.bankcards.dto.card_request.CardRequestDto;
import com.example.bankcards.dto.card_request.FilterCardRequestDto;
import com.example.bankcards.dto.card_request.ProcessCardRequestDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.card.Card;
import com.example.bankcards.entity.card.CardRequest;
import com.example.bankcards.entity.card.CardRequestStatus;
import com.example.bankcards.entity.card.CardRequestType;
import com.example.bankcards.entity.filter.builder.card_request.AdminCardRequestFilterBuilderInterface;
import com.example.bankcards.mapper.CardRequestMapper;
import com.example.bankcards.policy.card_request.BlockCardRequestPolicy;
import com.example.bankcards.policy.card_request.ProceedCardRequestPolicy;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.CardRequestRepository;
import com.example.bankcards.service.card_request.CardRequestServiceImpl;
import com.example.bankcards.service.card_request.handler.CardRequestHandler;
import com.example.bankcards.util.auth.AuthUserContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CardRequestServiceImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID CARD_ID = UUID.randomUUID();
    private static final UUID CARD_REQUEST_ID = UUID.randomUUID();
    private static final String IDEMPOTENCY_KEY = "TEST";

    @InjectMocks
    private CardRequestServiceImpl service;

    @Mock
    private CardRequestRepository cardRequestRepository;
    @Mock
    private CardRepository cardRepository;
    @Mock
    private CardRequestMapper cardRequestMapper;
    @Mock
    private Map<CardRequestType, CardRequestHandler> handlers;
    @Mock
    private AuthUserContext authUserContext;
    @Mock
    private BlockCardRequestPolicy blockCardRequestPolicy;
    @Mock
    private ProceedCardRequestPolicy proceedCardRequestPolicy;
    @Mock
    private AdminCardRequestFilterBuilderInterface adminCardRequestFilterBuilder;

    @Mock
    private CardRequestHandler blockHandler;
    @Mock
    private CardRequestHandler unblockHandler;

    @Mock
    private User currentUser;
    @Mock
    private CardRequest cardRequestMock;

    @DisplayName("Should return user's paginated and filtered card request list")
    @Test
    public void testGetMethod() {
        FilterCardRequestDto dto = mock(FilterCardRequestDto.class);
        Pageable pageable = mock(Pageable.class);
        Specification<CardRequest> specification = mock(Specification.class);
        Page<CardRequest> page = mock(Page.class);

        when(adminCardRequestFilterBuilder.buildSpecification(any(FilterCardRequestDto.class), isNull()))
                .thenReturn(specification);
        when(cardRequestRepository.findAll(specification, pageable)).thenReturn(page);

        service.get(dto, pageable);

        verify(adminCardRequestFilterBuilder, times(1))
                .buildSpecification(any(), any());
        verify(cardRequestMapper, times(page.getSize())).toCardRequestDto(any());
    }

    @DisplayName("Should block user card")
    @Test
    public void testSuccessfulBlockCardMethod() {
        BlockCardRequestDto dto = BlockCardRequestDto.builder()
                .cardId(CARD_ID)
                .idempotencyKey(IDEMPOTENCY_KEY)
                .build();

        Card card = mock(Card.class);
        when(authUserContext.getUserId()).thenReturn(USER_ID);
        when(cardRepository.findByIdOrThrow(dto.cardId())).thenReturn(card);

        service.block(dto);

        verify(blockCardRequestPolicy, times(1)).validate(card, USER_ID);
        verify(cardRequestRepository, times(1)).save(any(CardRequest.class));
    }

    @DisplayName("should call handler, set APPROVED and save")
    @Test
    void testSuccessfulApproveCardRequest() {
        CardRequestDto mappedDto = mock(CardRequestDto.class);
        ProcessCardRequestDto dto = new ProcessCardRequestDto(ProcessCardRequestDto.CardRequestProceedStatus.APPROVE);

        when(authUserContext.getUser()).thenReturn(currentUser);
        when(cardRequestRepository.findByIdOrThrow(CARD_ID)).thenReturn(cardRequestMock);
        when(cardRequestMock.getType()).thenReturn(CardRequestType.BLOCK);
        when(handlers.get(CardRequestType.BLOCK)).thenReturn(blockHandler);
        doNothing().when(proceedCardRequestPolicy).validate(cardRequestMock);
        when(cardRequestRepository.save(cardRequestMock)).thenReturn(cardRequestMock);
        when(cardRequestMapper.toCardRequestDto(cardRequestMock)).thenReturn(mappedDto);

        CardRequestDto result = service.process(CARD_ID, dto);

        assertEquals(mappedDto, result);
        verify(proceedCardRequestPolicy, times(1)).validate(cardRequestMock);
        verify(cardRequestMock).setStatus(CardRequestStatus.APPROVED);
        verify(blockHandler, times(1)).onApprove(cardRequestMock, currentUser);
        verify(cardRequestRepository, times(1)).save(cardRequestMock);
        verify(cardRequestMapper, times(1)).toCardRequestDto(cardRequestMock);
    }

    @DisplayName("should set REJECTED, set user and not call handler")
    @Test
    void testSuccessfulRejectCardRequest() {
        ProcessCardRequestDto dto = new ProcessCardRequestDto(ProcessCardRequestDto.CardRequestProceedStatus.REJECT);
        CardRequestDto mappedDto = mock(CardRequestDto.class);

        when(authUserContext.getUser()).thenReturn(currentUser);
        when(cardRequestRepository.findByIdOrThrow(CARD_REQUEST_ID)).thenReturn(cardRequestMock);
        doNothing().when(proceedCardRequestPolicy).validate(cardRequestMock);
        when(cardRequestRepository.save(cardRequestMock)).thenReturn(cardRequestMock);
        when(cardRequestMapper.toCardRequestDto(cardRequestMock)).thenReturn(mappedDto);

        CardRequestDto result = service.process(CARD_REQUEST_ID, dto);

        assertEquals(mappedDto, result);
        verify(proceedCardRequestPolicy).validate(cardRequestMock);
        verify(cardRequestMock).setStatus(CardRequestStatus.REJECTED);
        verify(cardRequestMock).setUser(currentUser);
        verifyNoInteractions(blockHandler);
        verify(cardRequestRepository).save(cardRequestMock);
        verify(cardRequestMapper).toCardRequestDto(cardRequestMock);
    }
}
