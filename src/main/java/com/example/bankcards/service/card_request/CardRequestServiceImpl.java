package com.example.bankcards.service.card_request;

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
import com.example.bankcards.exception.api.ConflictException;
import com.example.bankcards.mapper.CardRequestMapper;
import com.example.bankcards.policy.card_request.BlockCardRequestPolicy;
import com.example.bankcards.policy.card_request.ProceedCardRequestPolicy;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.CardRequestRepository;
import com.example.bankcards.service.card_request.handler.CardRequestHandler;
import com.example.bankcards.util.auth.AuthUserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CardRequestServiceImpl implements CardRequestService {
    private final CardRequestRepository cardRequestRepository;
    private final CardRepository cardRepository;
    private final CardRequestMapper cardRequestMapper;
    private final Map<CardRequestType, CardRequestHandler> handlers;
    private final AuthUserContext authUserContext;
    private final BlockCardRequestPolicy blockCardRequestPolicy;
    private final ProceedCardRequestPolicy proceedCardRequestPolicy;
    private final AdminCardRequestFilterBuilderInterface adminCardRequestFilterBuilder;

    @Override
    public Page<CardRequestDto> get(FilterCardRequestDto dto, Pageable pageable) {
        Specification<CardRequest> specification = adminCardRequestFilterBuilder
                .buildSpecification(dto, null);

        Page<CardRequest> cardRequests = cardRequestRepository.findAll(specification, pageable);
        return cardRequests.map(cardRequestMapper::toCardRequestDto);
    }

    @Override
    public CardRequestDto block(BlockCardRequestDto dto) {
        UUID userId = authUserContext.getUserId();
        Card card = cardRepository.findByIdOrThrow(dto.cardId());

        blockCardRequestPolicy.validate(card, userId);
        CardRequest entity = make(card, CardRequestType.BLOCK, dto.idempotencyKey());
        entity = cardRequestRepository.save(entity);

        return cardRequestMapper.toCardRequestDto(entity);
    }

    @Transactional
    @Override
    public CardRequestDto process(UUID id, ProcessCardRequestDto dto) {
        User user = authUserContext.getUser();
        CardRequest cardRequest = cardRequestRepository.findByIdOrThrow(id);

        proceedCardRequestPolicy.validate(cardRequest);
        if (dto.status().equals(ProcessCardRequestDto.CardRequestProceedStatus.REJECT)) {
            cardRequest.setStatus(CardRequestStatus.REJECTED);
            cardRequest.setUser(user);
        } else if (dto.status().equals(ProcessCardRequestDto.CardRequestProceedStatus.APPROVE)) {
            CardRequestHandler handler = getHandler(cardRequest.getType());
            cardRequest.setStatus(CardRequestStatus.APPROVED);
            handler.onApprove(cardRequest, user);
        }

        cardRequest = cardRequestRepository.save(cardRequest);

        return cardRequestMapper.toCardRequestDto(cardRequest);
    }

    private CardRequest make(Card card, CardRequestType type, String idempotencyKey) {
        return CardRequest.builder()
                .card(card)
                .type(type)
                .status(CardRequestStatus.PENDING)
                .idempotencyKey(idempotencyKey)
                .build();
    }

    private CardRequestHandler getHandler(CardRequestType type) {
        CardRequestHandler handler = handlers.get(type);
        if (handler == null) {
            throw new ConflictException(
                    "Couldn't proceed request",
                    String.format("CardRequestHandler not found, type: %s", type)
            );
        }
        return handler;
    }
}
