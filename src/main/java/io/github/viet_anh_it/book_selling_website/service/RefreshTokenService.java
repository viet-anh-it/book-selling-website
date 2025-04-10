package io.github.viet_anh_it.book_selling_website.service;

import java.time.Instant;
import java.util.Optional;

import io.github.viet_anh_it.book_selling_website.model.RefreshToken;

public interface RefreshTokenService {

    RefreshToken save(RefreshToken refreshTokenEntity);

    Optional<RefreshToken> findByTokenValue(String tokenValue);

    Optional<RefreshToken> findByTokenValueAndUserId(String tokenValue, long userId);

    boolean existsByJti(String jti);

    Optional<RefreshToken> findByJti(String jti);

    Optional<RefreshToken> findByTokenValueAndUserIdAndJti(String tokenValue, long userId, String jti);

    void deleteAllExpiredRefreshTokens(Instant now);

    // void deleteAllByUserEmail(String userEmail);

    void deleteAll(Iterable<? extends RefreshToken> refreshTokens);

    void delete(RefreshToken refreshTokenEntity);
}
