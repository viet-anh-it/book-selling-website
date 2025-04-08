package io.github.viet_anh_it.book_selling_website.service.impl;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Service;

import io.github.viet_anh_it.book_selling_website.model.BlackListedAccessToken;
import io.github.viet_anh_it.book_selling_website.repository.BlackListedAccessTokenRepository;
import io.github.viet_anh_it.book_selling_website.service.BlackListedAccessTokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BlackListedAccessTokenServiceImpl implements BlackListedAccessTokenService {

    BlackListedAccessTokenRepository blackListedAccessTokenRepository;

    @Override
    public BlackListedAccessToken save(BlackListedAccessToken blackListedAccessToken) {
        return this.blackListedAccessTokenRepository.save(blackListedAccessToken);
    }

    @Override
    public void deleteAllExpiredBlackListedAccessTokens(Instant now) {
        this.blackListedAccessTokenRepository.deleteAllExpiredBlackListedAccessTokens(now);
    }

    @Override
    public Optional<BlackListedAccessToken> findByJti(String jti) {
        return this.blackListedAccessTokenRepository.findByJti(jti);
    }

    @Override
    public boolean existsByJti(String jti) {
        return this.blackListedAccessTokenRepository.existsByJti(jti);
    }

}
