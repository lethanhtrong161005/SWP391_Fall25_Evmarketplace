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

    public static Specification<SaleOrder> ofBuyer(Long buyerId) {
        return (root, q, cb) -> (buyerId == null) ? null
                : cb.equal(root.get("buyer").get("id"), buyerId);
    }

    public static Specification<SaleOrder> createdBetween(LocalDateTime start, LocalDateTime end) {
        final LocalDateTime from = (start != null && end != null && end.isBefore(start)) ? end : start;
        final LocalDateTime to = (start != null && end != null && end.isBefore(start)) ? start : end;

        return (root, q, cb) -> {
            if (from == null && to == null) return null;
            if (from != null && to != null) return cb.between(root.get("createdAt"), from, to);
            return (from != null)
                    ? cb.greaterThanOrEqualTo(root.get("createdAt"), from)
                    : cb.lessThanOrEqualTo(root.get("createdAt"), to);
        };
    }

    public static Specification<SaleOrder> hasStatus(OrderStatus status) {
        return (root, q, cb) -> (status == null) ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<SaleOrder> orderNoEqualsIgnoreCase(String orderNo) {
        return (root, q, cb) -> {
            if (orderNo == null || orderNo.isBlank()) return null;
            return cb.equal(cb.lower(root.get("orderNo")), orderNo.trim().toLowerCase());
        };
    }

    public static Specification<SaleOrder> orderNoLike(String orderNo) {
        return (root, q, cb) ->
                (orderNo == null || orderNo.isBlank())
                        ? null
                        : cb.like(cb.lower(root.get("orderNo")), "%" + orderNo.trim().toLowerCase() + "%");
    }


}
