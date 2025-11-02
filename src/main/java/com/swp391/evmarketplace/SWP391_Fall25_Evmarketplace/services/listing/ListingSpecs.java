// services/listing/ListingSpecs.java
package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.listing;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.listing.ConsignmentListingFilter;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Listing;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ItemType;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ListingStatus;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class ListingSpecs {
    private ListingSpecs() {}

    public static Specification<Listing> consignmentFilter(ConsignmentListingFilter f) {
        if (f == null) f = new ConsignmentListingFilter();

        return Specification.allOf(
                isConsigned(),
                // equality
                eqCategoryIdIfNotNull(f.getCategoryId()),   // <-- FIX: category.id
                eqIfNotNull("brandId", f.getBrandId()),
                eqIfNotNull("modelId", f.getModelId()),
                eqIfNotNull("status", f.getStatus()),
                eqIfNotNull("visibility", f.getVisibility()),

                // common ranges
                rangeBigDecimal("price", f.getPriceMin(), f.getPriceMax()),
                rangeInteger("year", f.getYearMin(), f.getYearMax()),
                leInteger("mileageKm", f.getMileageMax()),
                geBigDecimal("sohPercent", f.getSohMin()),

                // text search
                fullTextLike(f.getQ()),

                // item-type specific
                itemTypeIfNotNull(f.getItemType()),            // <-- FIX: dùng field quan hệ
                rangeBigDecimal("batteryCapacityKwh", f.getBatteryCapacityMinKwh(), f.getBatteryCapacityMaxKwh()),
                rangeBigDecimal("voltage", f.getVoltageMinV(), f.getVoltageMaxV()), // <-- FIX: voltage
                leBigDecimal("massKg", f.getMassMaxKg()),
                inChemistries(f.getChemistries())
        );
    }

    /** Scope theo vai trò */
    public static Specification<Listing> scopeByRole(String role, Long viewerId, Long viewerBranchId) {
        if (role == null) return guestPublicScope();

        return switch (role) {
            case "ADMIN" -> (root, q, cb) -> cb.conjunction();
            case "MANAGER" -> {
                if (viewerBranchId == null) yield (root, q, cb) -> cb.disjunction();
                yield (root, q, cb) -> cb.equal(root.get("branch").get("id"), viewerBranchId);
            }
            case "STAFF" -> (root, q, cb) -> cb.equal(root.get("responsibleStaff").get("id"), viewerId);
            case "MEMBER" -> (root, q, cb) -> cb.conjunction();   // thấy tất cả consigned (đã được isConsigned() ở trên)
            default -> guestPublicScope();
        };
    }

    private static Specification<Listing> guestPublicScope() {
        return (root, q, cb) -> cb.and(
                cb.isTrue(root.get("consigned")),                   // <-- FIX: consigned
                cb.isTrue(root.get("verified")),
                cb.equal(root.get("status"), ListingStatus.ACTIVE)
        );
    }

    // ===== helpers =====
    private static Specification<Listing> isConsigned() {
        return (root, q, cb) -> cb.isTrue(root.get("consigned"));   // <-- FIX: consigned
    }

    private static <T> Specification<Listing> eqIfNotNull(String field, T value) {
        return (root, q, cb) -> value == null ? null : cb.equal(root.get(field), value);
    }

    /** So sánh category.id = ? (vì entity là ManyToOne) */
    private static Specification<Listing> eqCategoryIdIfNotNull(Long categoryId) {
        return (root, q, cb) -> (categoryId == null)
                ? null
                : cb.equal(root.get("category").get("id"), categoryId);
    }

    private static Specification<Listing> rangeInteger(String field, Integer min, Integer max) {
        return (root, q, cb) -> {
            List<Predicate> ps = new ArrayList<>();
            if (min != null) ps.add(cb.greaterThanOrEqualTo(root.get(field), min));
            if (max != null) ps.add(cb.lessThanOrEqualTo(root.get(field), max));
            return ps.isEmpty() ? null : cb.and(ps.toArray(new Predicate[0]));
        };
    }

    private static Specification<Listing> rangeBigDecimal(String field, BigDecimal min, BigDecimal max) {
        return (root, q, cb) -> {
            List<Predicate> ps = new ArrayList<>();
            if (min != null) ps.add(cb.greaterThanOrEqualTo(root.get(field), min));
            if (max != null) ps.add(cb.lessThanOrEqualTo(root.get(field), max));
            return ps.isEmpty() ? null : cb.and(ps.toArray(new Predicate[0]));
        };
    }

    private static Specification<Listing> geBigDecimal(String field, BigDecimal min) {
        return (root, q, cb) -> min == null ? null : cb.greaterThanOrEqualTo(root.get(field), min);
    }

    private static Specification<Listing> leBigDecimal(String field, BigDecimal max) {
        return (root, q, cb) -> max == null ? null : cb.lessThanOrEqualTo(root.get(field), max);
    }

    private static Specification<Listing> leInteger(String field, Integer max) {
        return (root, q, cb) -> max == null ? null : cb.lessThanOrEqualTo(root.get(field), max);
    }

    private static Specification<Listing> itemTypeIfNotNull(ItemType t) {
        if (t == null) return null;
        return (root, q, cb) -> switch (t) {
            case VEHICLE -> cb.isNotNull(root.get("productVehicle"));  // <-- FIX
            case BATTERY -> cb.isNotNull(root.get("productBattery"));  // <-- FIX
        };
    }

    private static Specification<Listing> inChemistries(Set<String> chems) {
        if (chems == null || chems.isEmpty()) return null;
        return (root, q, cb) -> {
            Expression<String> field = cb.lower(root.get("batteryChemistry"));
            CriteriaBuilder.In<String> in = cb.in(field);
            for (String ch : chems) {
                if (ch != null && !ch.isBlank()) in.value(ch.toLowerCase().trim());
            }
            return in;
        };
    }

    private static Specification<Listing> fullTextLike(String qStr) {
        if (qStr == null || qStr.isBlank()) return null;
        String pat = "%" + qStr.trim().toLowerCase() + "%";

        return (root, q, cb) -> {
            // varchar: OK dùng lower trực tiếp
            var title = cb.lower(root.get("title"));
            var brand = cb.lower(root.get("brand"));
            var model = cb.lower(root.get("model"));

            // CLOB/TEXT: ép về String trước, rồi lower; kèm coalesce để tránh NULL
            var descStr = cb.coalesce(root.get("description").as(String.class), "");
            var desc    = cb.lower(descStr);

            return cb.or(
                    cb.like(title, pat),
                    cb.like(brand, pat),
                    cb.like(model, pat),
                    cb.like(desc, pat)
            );
        };
    }

}
