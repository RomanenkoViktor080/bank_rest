package com.example.bankcards.service;

import com.example.bankcards.dto.card.AdminCardFilterDto;
import com.example.bankcards.dto.card.CreateCardDto;
import com.example.bankcards.dto.card.UserCardFilterDto;
import com.example.bankcards.entity.card.Card;
import com.example.bankcards.entity.filter.builder.card.admin.AdminCardFilterBuilderInterface;
import com.example.bankcards.entity.filter.builder.card.user.UserCardFilterBuilderInterface;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.policy.card.CardCreationPolicy;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.card.CardServiceImpl;
import com.example.bankcards.util.auth.AuthUserContext;
import com.example.bankcards.util.crypto.Sha256;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CardServiceImplTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @InjectMocks
    private CardServiceImpl service;

    @Mock
    private CardCreationPolicy cardCreationPolicy;
    @Spy
    private CardMapper cardMapper = Mappers.getMapper(CardMapper.class);
    @Mock
    private CardRepository cardRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private Sha256 sha256;
    @Mock
    private UserCardFilterBuilderInterface cardFilterBuilder;
    @Mock
    private AdminCardFilterBuilderInterface adminCardFilterBuilder;
    @Mock
    private AuthUserContext authContext;

    @Captor
    ArgumentCaptor<UserCardFilterDto> userFilterCaptor;
    @Captor
    ArgumentCaptor<AdminCardFilterDto> adminFilterCaptor;
    @Captor
    ArgumentCaptor<Function<Specification<Card>, Specification<Card>>> funcCaptor;

    @DisplayName("Should return all user's paginated and filtered card list")
    @Test
    public void shouldReturnFilteredPaginatedAllUserCards() {
        AdminCardFilterDto dto = mock(AdminCardFilterDto.class);
        Pageable pageable = mock(Pageable.class);
        Specification<Card> specification = mock(Specification.class);
        Page<Card> page = mock(Page.class);

        when(adminCardFilterBuilder.buildSpecification(any(AdminCardFilterDto.class), isNull())).thenReturn(specification);
        when(cardRepository.findAll(specification, pageable)).thenReturn(page);

        service.get(dto, pageable);

        verify(adminCardFilterBuilder, times(1))
                .buildSpecification(adminFilterCaptor.capture(), funcCaptor.capture());
        verify(cardMapper, times(page.getSize())).toCardDto(any());
        assertEquals(adminFilterCaptor.getValue(), dto);
    }


    @DisplayName("Should return user's paginated and filtered card list")
    @Test
    public void shouldReturnFilteredPaginatedUserCards() {
        UserCardFilterDto dto = mock(UserCardFilterDto.class);
        Pageable pageable = mock(Pageable.class);
        Specification<Card> specification = mock(Specification.class);
        Page<Card> page = mock(Page.class);

        when(authContext.getUserId()).thenReturn(USER_ID);
        when(cardFilterBuilder.buildSpecification(any(UserCardFilterDto.class), any())).thenReturn(specification);
        when(cardRepository.findAll(specification, pageable)).thenReturn(page);

        service.get(dto, pageable);

        verify(cardFilterBuilder, times(1))
                .buildSpecification(userFilterCaptor.capture(), funcCaptor.capture());
        verify(cardMapper, times(page.getSize())).toCardDto(any());
        assertEquals(userFilterCaptor.getValue(), dto);
    }

    @DisplayName("Should return created card dto")
    @Test
    public void shouldReturnCreatedUserCardDto() {
        String hash = "hash";
        boolean isExists = true;
        CreateCardDto dto = CreateCardDto.builder()
                .userId(USER_ID)
                .expiryMonth(12)
                .expiryYear(34)
                .firstNameSnapshot("EXAMPLE")
                .lastNameSnapshot("EXAMPLE")
                .pan("4242424242424242")
                .build();
        when(sha256.encrypt(dto.pan())).thenReturn(hash);
        when(cardRepository.existsByPanHash(hash)).thenReturn(isExists);

        service.create(dto);

        verify(sha256, times(1)).encrypt(dto.pan());
        verify(cardRepository, times(1)).existsByPanHash(hash);
        verify(userRepository, times(1)).findByIdOrThrow(dto.userId());
        verify(cardCreationPolicy, times(1)).validate(dto, isExists);
        verify(cardMapper, times(1)).toCard(eq(dto), eq(hash), any());
        verify(cardRepository, times(1)).save(any());
    }
}
