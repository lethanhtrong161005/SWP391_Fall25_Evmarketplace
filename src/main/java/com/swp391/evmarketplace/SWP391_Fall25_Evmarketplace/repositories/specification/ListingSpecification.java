package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.specification;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Listing;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ListingSpecification {
    public static Specification<Listing> brand(String brand) {
        return (root, q, cb) -> (brand == null || brand.isBlank()) ? null
                : cb.equal(cb.lower(root.get("brand")), brand.toLowerCase());
    }

    public static Specification<Listing> model(String kw) {
        return (root, q, cb) -> (kw == null || kw.isBlank()) ? null
                : cb.like(cb.lower(root.get("model")), "%" + kw.toLowerCase() + "%");
    }

    public static Specification<Listing> yearFrom(Integer y) {
        return (root, q, cb) -> y == null ? null : cb.greaterThanOrEqualTo(root.get("year"), y);
    }

    public static Specification<Listing> yearTo(Integer y) {
        return (root, q, cb) -> y == null ? null : cb.lessThanOrEqualTo(root.get("year"), y);
    }

    public static Specification<Listing> capacityMin(BigDecimal v) {
        return (root, q, cb) -> v == null ? null : cb.greaterThanOrEqualTo(root.get("batteryCapacityKwh"), v);
    }

    public static Specification<Listing> capacityMax(BigDecimal v) {
        return (root, q, cb) -> v == null ? null : cb.lessThanOrEqualTo(root.get("batteryCapacityKwh"), v);
    }

    public static Specification<Listing> priceMin(BigDecimal v) {
        return (root, q, cb) -> v == null ? null : cb.greaterThanOrEqualTo(root.get("price"), v);
    }

    public static Specification<Listing> priceMax(BigDecimal v) {
        return (root, q, cb) -> v == null ? null : cb.lessThanOrEqualTo(root.get("price"), v);
    }

    public static Specification<Listing> mileageMin(Integer v) {
        return (root, q, cb) -> v == null ? null : cb.greaterThanOrEqualTo(root.get("mileageKm"), v);
    }

    public static Specification<Listing> mileageMax(Integer v) {
        return (root, q, cb) -> v == null ? null : cb.lessThanOrEqualTo(root.get("mileageKm"), v);
    }

    public static Specification<Listing> sohMin(BigDecimal v) {
        return (root, q, cb) -> v == null ? null : cb.greaterThanOrEqualTo(root.get("sohPercent"), v);
    }

    public static Specification<Listing> sohMax(BigDecimal v) {
        return (root, q, cb) -> v == null ? null : cb.lessThanOrEqualTo(root.get("sohPercent"), v);
    }
}
