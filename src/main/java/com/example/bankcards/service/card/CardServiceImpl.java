package com.example.bankcards.service.card;

import com.example.bankcards.dto.card.AdminCardFilterDto;
import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.dto.card.CreateCardDto;
import com.example.bankcards.dto.card.CardFilterDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.card.Card;
import com.example.bankcards.entity.card.CardStatus;
import com.example.bankcards.entity.filter.builder.card.admin.AdminCardFilterBuilderInterface;
import com.example.bankcards.entity.filter.builder.card.user.UserCardFilterBuilderInterface;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.policy.card.CardCreationPolicy;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.auth.AuthUserContext;
import com.example.bankcards.util.crypto.Sha256;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CardServiceImpl implements CardService {
    private static final int LAST4 = 4;

    private final CardCreationPolicy cardCreationPolicy;
    private final CardMapper cardMapper;
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final Sha256 sha256;
    private final UserCardFilterBuilderInterface cardFilterBuilder;
    private final AdminCardFilterBuilderInterface adminCardFilterBuilder;
    private final AuthUserContext authContext;

    @Override
    public Page<CardDto> get(AdminCardFilterDto dto, Pageable pageable) {
        Specification<Card> cardSpecification = adminCardFilterBuilder.buildSpecification(dto, null);

        return cardRepository.findAll(cardSpecification, pageable)
                .map(cardMapper::toCardDto);
    }

    @Override
    public Page<CardDto> get(CardFilterDto dto, Pageable pageable) {
        UUID userId = authContext.getUserId();

        Specification<Card> cardSpecification = cardFilterBuilder.buildSpecification(
                dto, spec -> spec.and((root, query, cb) ->
                        cb.equal(root.get("user").get("id"), userId))
        );

        return cardRepository.findAll(cardSpecification, pageable)
                .map(cardMapper::toCardDto);
    }

    @Override
    public CardDto create(CreateCardDto dto) {
        String last4 = dto.pan().substring(dto.pan().length() - LAST4);
        String hash = sha256.encrypt(dto.pan());
        boolean isExists = cardRepository.existsByPanHash(hash);
        User user = userRepository.findByIdOrThrow(dto.userId());
        cardCreationPolicy.validate(dto, isExists);

        Card card = cardMapper.toCard(dto, hash, last4);
        card.setUser(user);
        card.setBalance(BigDecimal.ZERO);
        card.setStatus(CardStatus.ACTIVE);
        card = cardRepository.save(card);

        return cardMapper.toCardDto(card);
    }

    @Override
    public void delete(UUID id) {
        //Добавить аудит, чтобы можно отследить кто удалил
        cardRepository.deleteById(id);
    }
}
