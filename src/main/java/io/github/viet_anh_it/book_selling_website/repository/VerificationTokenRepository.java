package io.github.viet_anh_it.book_selling_website.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.github.viet_anh_it.book_selling_website.model.VerificationToken;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    boolean existsByTokenValue(String tokenValue);

    Optional<VerificationToken> findByTokenValue(String verificationTokenValue);

    @Modifying
    @Transactional
    @Query("delete from VerificationToken vt where vt.expiresAt <= :now")
    void deleteAllExpiredVerificationToken(@Param("now") Instant now);

}
