package io.github.viet_anh_it.book_selling_website.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.github.viet_anh_it.book_selling_website.model.RefreshToken;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenValue(String refreshTokenValue);

    @Query("select rt from RefreshToken rt where rt.tokenValue = :tokenValue and rt.user.id = :userId")
    Optional<RefreshToken> findByTokenValueAndUserId(@Param("tokenValue") String tokenValue,
            @Param("userId") long userId);

    boolean existsByJti(String jti);

    Optional<RefreshToken> findByJti(String jti);

    @Query("select rt from RefreshToken rt where rt.tokenValue = :tokenValue and rt.jti = :jti and rt.user.id = :userId")
    Optional<RefreshToken> findByTokenValueAndUserIdAndJti(@Param("tokenValue") String tokenValue,
            @Param("userId") long userId, @Param("jti") String jti);

    @Modifying
    @Transactional
    @Query("delete from RefreshToken rt where rt.expiresAt <= :now")
    void deleteAllExpiredRefreshTokens(@Param("now") Instant now);
}