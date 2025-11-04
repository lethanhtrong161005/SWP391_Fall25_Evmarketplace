package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ConsignmentPayout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConsignmentPayoutRepository extends JpaRepository<ConsignmentPayout, Long> {
    boolean existsBySettlementId(Long settlement);
    Optional<ConsignmentPayout> findBySaleId(Long saleId);
}
