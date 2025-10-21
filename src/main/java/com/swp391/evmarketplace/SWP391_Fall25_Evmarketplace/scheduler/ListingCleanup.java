package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.scheduler;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ListingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class ListingCleanup {

    @Autowired
    private ListingRepository listingRepository;

    @Value("${jobs.listing-cleanup.enabled}")
    private boolean enabled;

    @Value("${jobs.listing-cleanup.days:30}")
    private int retentionDays;

    @Scheduled(cron = "${jobs.listing-cleanup.cron}", zone = "Asia/Ho_Chi_Minh")
    public void purge() {
        if (!enabled) {
            log.debug("[ListingCleanupJob] Disabled, skip.");
            return;
        }
        try {
            long candidates = listingRepository.countPurgeCandidates(retentionDays);
            int deleted = listingRepository.hardDeleteSoftDeletedOlderThan(retentionDays);
            log.info("[ListingCleanupJob] Purged {}/{} listings older than {} days (SOFT_DELETED).",
                    deleted, candidates, retentionDays);
        } catch (Exception ex) {
            log.error("[ListingCleanupJob] Failed: {}", ex.getMessage(), ex);
        }
    }

    @Scheduled(
            fixedDelayString = "${moderation.lock.reaper.fixed-delay-ms:30000}",
            initialDelayString = "${moderation.lock.reaper.initial-delay-ms:15000}"
    )
    @Transactional
    public void releaseExpiredLocks() {
        int freed = listingRepository.releaseExpiredModerationLocks();
        if (freed > 0) {
            log.info("Released {} expired moderation locks", freed);
        }
    }

}


