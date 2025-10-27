package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.order;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Listing;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.SaleOrder;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.OrderStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public final class SaleOrderSpecs {
    private SaleOrderSpecs() {}

    public static Specification<SaleOrder> ofStaff(Long staffId) {
        return (root, q, cb) -> {
            if (staffId == null) return null; // để allOf(...) bỏ qua khi null
            var listing = root.join("listing", JoinType.INNER);
            var staff = listing.join("responsibleStaff", JoinType.INNER);
            return cb.equal(staff.get("id"), staffId);
        };
    }

    public static Specification<SaleOrder> hasStatus(OrderStatus status) {
        return (root, q, cb) -> (status == null) ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<SaleOrder> createdFrom(LocalDateTime start) {
        return (root, q, cb) -> (start == null) ? null : cb.greaterThanOrEqualTo(root.get("createdAt"), start);
    }

    public static Specification<SaleOrder> createdTo(LocalDateTime end) {
        return (root, q, cb) -> (end == null) ? null : cb.lessThanOrEqualTo(root.get("createdAt"), end);
    }

    public static Specification<SaleOrder> orderNoEquals(String orderNo) {
        return (root, q, cb) ->
                (orderNo == null || orderNo.isBlank()) ? null : cb.equal(root.get("orderNo"), orderNo.trim());
    }

    public static Specification<SaleOrder> orderNoLike(String orderNo) {
        return (root, q, cb) ->
                (orderNo == null || orderNo.isBlank())
                        ? null
                        : cb.like(cb.lower(root.get("orderNo")), "%" + orderNo.trim().toLowerCase() + "%");
    }

}
