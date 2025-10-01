package com.example.bankcards.service;

import com.example.bankcards.dto.user.UserDto;
import com.example.bankcards.dto.user.UserFilterDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.filter.builder.user.UserFilterBuilderInterface;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.policy.user.UserBanPolicy;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.user.UserServiceImpl;
import com.example.bankcards.util.auth.AuthUserContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    private static final UUID CURRENT_USER_ID = UUID.randomUUID();

    @InjectMocks
    private UserServiceImpl service;

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserFilterBuilderInterface userFilterBuilder;
    @Mock
    private UserBanPolicy userBanPolicy;
    @Mock
    private AuthUserContext authContext;

    @Captor
    private ArgumentCaptor<Specification<User>> specCaptor;
    @Captor
    private ArgumentCaptor<Pageable> pageableCaptor;

    @DisplayName("test get list of user")
    @Test
    void testGet() {
        UserFilterDto filter = new UserFilterDto("search");
        Pageable pageable = PageRequest.of(0, 10);
        User u1 = mock(User.class);
        User u2 = mock(User.class);
        Page<User> page = new PageImpl<>(List.of(u1, u2), pageable, 2);
        Specification<User> spec = mock(Specification.class);

        when(userFilterBuilder.buildSpecification(eq(filter), any())).thenReturn(spec);
        when(userRepository.findAll(eq(spec), eq(pageable))).thenReturn(page);

        Page<UserDto> result = service.get(filter, pageable);

        verify(userFilterBuilder, times(1)).buildSpecification(eq(filter), any());
        verify(userRepository, times(1)).findAll(specCaptor.capture(), pageableCaptor.capture());
        assertEquals(page.getSize(), result.getSize());
    }

    @DisplayName("test successful ban")
    @Test
    void testSuccessfulBan() {
        User user = mock(User.class);

        when(authContext.getUserId()).thenReturn(CURRENT_USER_ID);
        when(userRepository.findByIdOrThrow(CURRENT_USER_ID)).thenReturn(user);
        doNothing().when(userBanPolicy).validate(user, CURRENT_USER_ID);
        when(userRepository.save(user)).thenReturn(user);

        UserDto result = service.ban(CURRENT_USER_ID);

        verify(userBanPolicy).validate(user, CURRENT_USER_ID);
        verify(user).setBanned(true);
        verify(userRepository).save(user);
        verify(userMapper).toUserDto(user);
    }
}
