package io.github.viet_anh_it.book_selling_website.service;

import java.time.Instant;
import java.util.Optional;

import io.github.viet_anh_it.book_selling_website.enums.TokenTypeEnum;
import io.github.viet_anh_it.book_selling_website.model.User;
import io.github.viet_anh_it.book_selling_website.model.VerificationToken;

public interface VerificationTokenService {

    VerificationToken save(VerificationToken verificationTokenEntity);

    boolean existsByTokenValue(String tokenValue);

    Optional<VerificationToken> findByTokenValue(String verificationTokenValue);

    void deleteAllExpiredVerificationToken(Instant now);

    void delete(VerificationToken verificationTokenEntity);

    VerificationToken createVerificationTokenForUser(User user, TokenTypeEnum verificationTokenType,
            long validDurationBySeconds);
}
