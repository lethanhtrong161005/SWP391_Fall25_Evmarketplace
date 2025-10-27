package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.SaleOrder;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.OrderStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface SaleOrderRepository extends
        JpaRepository<SaleOrder, Long>,
        JpaSpecificationExecutor<SaleOrder> {
    boolean existsByListingId(Long listingId);
    boolean existsByListing_IdAndIsOpenTrue(Long listingId);
    boolean existsByListing_IdAndStatusIn(Long listingId, Collection<OrderStatus> statuses);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from SaleOrder o where o.id = :id")
    Optional<SaleOrder> findByIdForUpdate(Long id);

    @EntityGraph(attributePaths = {
            "listing", "branch", "buyer", "buyer.profile", "seller", "seller.profile"
    })
    Page<SaleOrder> findByOrderNoAndListing_ResponsibleStaff_Id(
            String orderNo, Long staffId, Pageable pageable);

    @EntityGraph(attributePaths = {
            "listing", "branch",
            "buyer", "buyer.profile",
            "seller", "seller.profile"
    })
    Page<SaleOrder> findAll(Specification<SaleOrder> spec, Pageable pageable);

}
