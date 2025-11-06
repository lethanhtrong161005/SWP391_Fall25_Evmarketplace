package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ConsignmentSettlement;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.SettlementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsignmentSettlementRepository extends JpaRepository<ConsignmentSettlement, Long> {
    List<ConsignmentSettlement> findByStatus(SettlementStatus status);

}
