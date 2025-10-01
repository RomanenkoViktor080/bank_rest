package com.example.bankcards.policy.user;

import com.example.bankcards.entity.User;

import java.util.UUID;

public interface UserBanPolicy {
    void validate(User user, UUID currentUserId);
}
