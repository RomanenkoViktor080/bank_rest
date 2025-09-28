package com.example.bankcards.mapper;

import com.example.bankcards.dto.auth.SignUpRequest;
import com.example.bankcards.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface UserMapper {
    User toUser(SignUpRequest userDto);
}
