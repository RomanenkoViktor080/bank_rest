package com.example.bankcards.util.auth;

import com.example.bankcards.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthUserContextImpl implements AuthUserContext {
    public User getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException("User is not authenticated");
        }
        Object principal = auth.getPrincipal();
        if (!(principal instanceof User user)) {
            throw new AuthenticationServiceException(
                    "Expected principal of type User but found " + principal.getClass().getName()
            );
        }
        return user;
    }

    public UUID getUserId() {
        return getUser().getId();
    }
}
