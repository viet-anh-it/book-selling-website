package io.github.viet_anh_it.book_selling_website.scheduler;

import java.time.Instant;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.github.viet_anh_it.book_selling_website.service.BlackListedAccessTokenService;
import io.github.viet_anh_it.book_selling_website.service.RefreshTokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TokenScheduler {

    RefreshTokenService refreshTokenService;
    BlackListedAccessTokenService blackListedAccessTokenService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteAllExpiredRefreshTokens() {
        this.refreshTokenService.deleteAllExpiredRefreshTokens(Instant.now());
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteAllExpiredBlackListedAccessTokens() {
        this.blackListedAccessTokenService.deleteAllExpiredBlackListedAccessTokens(Instant.now());
    }

}
