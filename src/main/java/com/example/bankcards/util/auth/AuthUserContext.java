package com.example.bankcards.util.auth;

import com.example.bankcards.entity.User;

import java.util.UUID;

public interface AuthUserContext {
    User getUser();

    UUID getUserId();
}
