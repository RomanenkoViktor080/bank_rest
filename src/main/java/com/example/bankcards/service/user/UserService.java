package com.example.bankcards.service.user;

import com.example.bankcards.dto.user.UserDto;
import com.example.bankcards.dto.user.UserFilterDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    Page<UserDto> get(UserFilterDto dto, Pageable pageable);

    UserDto ban(UUID id);
}
