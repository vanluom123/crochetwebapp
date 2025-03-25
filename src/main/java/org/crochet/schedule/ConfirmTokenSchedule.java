package org.crochet.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.crochet.service.ConfirmTokenService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConfirmTokenSchedule {

    private final ConfirmTokenService confirmTokenService;

    @Scheduled(cron = "0 0 1 ? * MON") // Runs at 1:00 AM every Monday
    public void deleteExpiredOrConfirmedTokens() {
        log.info("Starting weekly cleanup of expired or confirmed tokens");
        confirmTokenService.deleteExpiredOrConfirmedTokens();
        log.info("Completed cleanup expired or confirmed token");
    }
}
