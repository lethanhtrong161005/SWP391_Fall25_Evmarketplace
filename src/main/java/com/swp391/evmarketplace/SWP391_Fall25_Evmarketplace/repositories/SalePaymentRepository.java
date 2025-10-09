package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.SalePayment;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.PaymentPurpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SalePaymentRepository extends JpaRepository<SalePayment, Long> {
    Optional<SalePayment> findByProviderTxnId(String providerTxnId);

    Optional<SalePayment> findTopByListing_IdAndPurposeOrderByIdDesc(
            Long listingId, PaymentPurpose purpose);
}
