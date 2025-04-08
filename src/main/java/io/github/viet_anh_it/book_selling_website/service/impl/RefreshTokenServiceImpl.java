package io.github.viet_anh_it.book_selling_website.service.impl;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Service;

import io.github.viet_anh_it.book_selling_website.model.RefreshToken;
import io.github.viet_anh_it.book_selling_website.repository.RefreshTokenRepository;
import io.github.viet_anh_it.book_selling_website.service.RefreshTokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RefreshTokenServiceImpl implements RefreshTokenService {

    RefreshTokenRepository refreshTokenRepository;

    @Override
    public RefreshToken save(RefreshToken refreshTokenEntity) {
        return this.refreshTokenRepository.save(refreshTokenEntity);
    }

    @Override
    public Optional<RefreshToken> findByTokenValueAndUserId(String tokenValue, long userId) {
        return this.refreshTokenRepository.findByTokenValueAndUserId(tokenValue, userId);
    }

    @Override
    public Optional<RefreshToken> findByJti(String jti) {
        return this.refreshTokenRepository.findByJti(jti);
    }

    @Override
    public void deleteAllExpiredRefreshTokens(Instant now) {
        this.refreshTokenRepository.deleteAllExpiredRefreshTokens(now);
    }

    @Override
    public void deleteAll(Iterable<? extends RefreshToken> refreshTokens) {
        this.refreshTokenRepository.deleteAll(refreshTokens);
    }

    @Override
    public void delete(RefreshToken refreshTokenEntity) {
        this.refreshTokenRepository.delete(refreshTokenEntity);
    }

    // @Override
    // public void deleteAllByUserEmail(String userEmail) {
    // this.deleteAllByUserEmail(userEmail);
    // }

}
