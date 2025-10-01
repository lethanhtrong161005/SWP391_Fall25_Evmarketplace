package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.scheduler;


import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.OtpRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class OtpCleanupScheduler {

    private final OtpRepository otpRepository;

    public OtpCleanupScheduler(OtpRepository otpRepository) {
        this.otpRepository = otpRepository;
    }

    /**
     * Xoá OTP hết hạn sau 1 phút nếu chưa dùng
     */
    @Transactional
    @Scheduled(fixedRate = 60000) // chạy mỗi 1 phút
    public void cleanupExpiredOtp() {
        otpRepository.deleteByExpiredAtBeforeAndIsUsedFalse(LocalDateTime.now());
    }

    /**
     * Xoá token hết hạn sau 10 phút
     */
    @Transactional
    @Scheduled(fixedRate = 60000) // chạy mỗi 1 phút
    public void cleanupExpiredToken() {
        otpRepository.deleteByTokenExpiredAtBefore(LocalDateTime.now());
    }

}
