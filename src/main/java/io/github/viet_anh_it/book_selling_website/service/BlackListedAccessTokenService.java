package io.github.viet_anh_it.book_selling_website.service;

import java.time.Instant;
import java.util.Optional;

import io.github.viet_anh_it.book_selling_website.model.BlackListedAccessToken;

public interface BlackListedAccessTokenService {

    BlackListedAccessToken save(BlackListedAccessToken accessTokenBlacklist);

    boolean existsByJti(String jti);

    Optional<BlackListedAccessToken> findByJti(String jti);

    void deleteAllExpiredBlackListedAccessTokens(Instant now);
}
