package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PhoneOtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findByPhoneNumber(String phoneNumber);

    Otp findByTempToken(String tempToken);

    void deleteByExpiredAtBeforeAndIsUsedFalse(LocalDateTime now);
    void deleteByTokenExpiredAtBefore(LocalDateTime now);

}
