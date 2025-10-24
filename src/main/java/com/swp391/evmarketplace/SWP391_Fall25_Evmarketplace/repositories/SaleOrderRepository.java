package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.SaleOrder;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SaleOrderRepository extends JpaRepository<SaleOrder, Long> {
    boolean existsByListingId(Long listingId);
    boolean existsByListing_IdAndIsOpenTrue(Long listingId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from SaleOrder o where o.id = :id")
    Optional<SaleOrder> findByIdForUpdate(Long id);
}
