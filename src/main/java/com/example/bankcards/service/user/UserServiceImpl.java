package com.example.bankcards.service.user;

import com.example.bankcards.dto.user.UserDto;
import com.example.bankcards.dto.user.UserFilterDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.filter.builder.user.UserFilterBuilderInterface;
import com.example.bankcards.entity.role.RoleType;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.policy.user.UserBanPolicy;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.auth.AuthUserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserFilterBuilderInterface userFilterBuilder;
    private final UserBanPolicy userBanPolicy;
    private final AuthUserContext authContext;

    @Override
    public Page<UserDto> get(UserFilterDto dto, Pageable pageable) {
        Specification<User> specification = userFilterBuilder.buildSpecification(dto, (spec) -> spec
                .and((root, query, cb) ->
                        cb.like(root.get("role").get("type"), RoleType.USER.toString())
                ));
        Page<User> users = userRepository.findAll(specification, pageable);

        return users.map(userMapper::toUserDto);
    }

    @Override
    public UserDto ban(UUID id) {
        UUID userId = authContext.getUserId();
        User user = userRepository.findByIdOrThrow(id);

        userBanPolicy.validate(user, userId);
        user.setBanned(true);
        user = userRepository.save(user);

        return userMapper.toUserDto(user);
    }
}
