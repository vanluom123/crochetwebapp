package org.crochet.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.crochet.service.RefreshTokenService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenSchedule {

    private final RefreshTokenService refreshTokenService;

    /**
     * Schedule to delete expired refresh tokens
     */
    @Scheduled(cron = "0 0 1 ? * MON")
    public void deleteExpiredRefreshTokens() {
        log.info("Starting weekly cleanup of expired refresh tokens");
        refreshTokenService.deleteExpiredRefreshTokens();
        log.info("Completed cleanup expired refresh tokens");
    }
}
