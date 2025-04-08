package io.github.viet_anh_it.book_selling_website.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.github.viet_anh_it.book_selling_website.model.BlackListedAccessToken;

@Repository
public interface BlackListedAccessTokenRepository extends JpaRepository<BlackListedAccessToken, Long> {

    boolean existsByJti(String jti);

    Optional<BlackListedAccessToken> findByJti(String jti);

    @Modifying
    @Transactional
    @Query("delete from BlackListedAccessToken blat where blat.expiresAt <= :now")
    void deleteAllExpiredBlackListedAccessTokens(@Param("now") Instant now);
}
