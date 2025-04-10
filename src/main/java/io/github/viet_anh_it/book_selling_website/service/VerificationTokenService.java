package io.github.viet_anh_it.book_selling_website.service;

import java.time.Instant;
import java.util.Optional;

import io.github.viet_anh_it.book_selling_website.model.VerificationToken;

public interface VerificationTokenService {

    VerificationToken save(VerificationToken verificationTokenEntity);

    Optional<VerificationToken> findByTokenValue(String verificationTokenValue);

    void deleteAllExpiredVerificationToken(Instant now);

    void delete(VerificationToken verificationTokenEntity);
}
