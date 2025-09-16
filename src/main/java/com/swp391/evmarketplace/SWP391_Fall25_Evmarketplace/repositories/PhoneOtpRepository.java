package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.PhoneOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PhoneOtpRepository extends JpaRepository<PhoneOtp, Long> {
    Optional<PhoneOtp> findByPhoneNumber(String phoneNumber);

    PhoneOtp findByTempToken(String tempToken);

    void deleteByExpiredAtBeforeAndIsUsedFalse(LocalDateTime now);
    void deleteByTokenExpiredAtBefore(LocalDateTime now);

}
