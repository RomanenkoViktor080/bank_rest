package com.example.bankcards.policy.user;

import com.example.bankcards.entity.User;
import com.example.bankcards.exception.api.DataValidationException;
import com.example.bankcards.exception.api.ForbiddenException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserBanPolicyImpl implements UserBanPolicy {
    @Override
    public void validate(User user, UUID currentUserId) {
        if (user.isBanned()) {
            throw new DataValidationException("User is already banned");
        }

        if (user.getId().equals(currentUserId)) {
            throw new ForbiddenException("Forbidden action: you cannot ban your own account");
        }
    }
}
