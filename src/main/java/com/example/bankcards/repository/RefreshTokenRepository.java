package com.example.bankcards.repository;

import com.example.bankcards.entity.RefreshToken;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {

    @Query(nativeQuery = true, value = """
            SELECT *
            FROM refresh_tokens
            WHERE token = :token
              AND is_revoked = false
              AND expired_at > now()
            """)
    Optional<RefreshToken> getValidToken(String token);

    void deleteByToken(String token);

}