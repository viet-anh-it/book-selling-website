package io.github.viet_anh_it.book_selling_website.service.impl;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Service;

import io.github.viet_anh_it.book_selling_website.model.VerificationToken;
import io.github.viet_anh_it.book_selling_website.repository.VerificationTokenRepository;
import io.github.viet_anh_it.book_selling_website.service.VerificationTokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VerificationTokenServiceImpl implements VerificationTokenService {

    VerificationTokenRepository verificationTokenRepository;

    @Override
    public VerificationToken save(VerificationToken verificationTokenEntity) {
        return this.verificationTokenRepository.save(verificationTokenEntity);
    }

    @Override
    public Optional<VerificationToken> findByTokenValue(String verificationTokenValue) {
        return this.verificationTokenRepository.findByTokenValue(verificationTokenValue);
    }

    @Override
    public void deleteAllExpiredVerificationToken(Instant now) {
        this.verificationTokenRepository.deleteAllExpiredVerificationToken(now);
    }

    @Override
    public void delete(VerificationToken verificationTokenEntity) {
        this.verificationTokenRepository.delete(verificationTokenEntity);
    }
}
