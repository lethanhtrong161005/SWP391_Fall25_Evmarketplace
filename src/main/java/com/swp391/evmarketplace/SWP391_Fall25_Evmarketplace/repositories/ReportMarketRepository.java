package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class ReportMarketRepository {

    private final EntityManager em;

    // 1) Phân loại loại bài: FREE vs BOOSTER (dựa vào is_promoted)
    public Map<String, Long> countPostType(LocalDate from, LocalDate to) {
        Query q = em.createNativeQuery("""
                    SELECT l.visibility, COUNT(*) AS cnt
                    FROM listing l
                    WHERE l.created_at >= :from AND l.created_at < :toPlus
                    AND l.status = 'ACTIVE'
                    GROUP BY visibility
                """);
        setRange(q, from, to);
        List<Object[]> rows = q.getResultList();
        Map<String, Long> map = new LinkedHashMap<>();
        for (Object[] r : rows)
            map.put(Objects.toString(r[0], "UNKNOWN"), ((Number) r[1]).longValue());
        return map;
    }

    // 2) Phân loại theo loại sản phẩm (category)
    public Map<String, Long> countByCategory(LocalDate from, LocalDate to) {
        Query q = em.createNativeQuery("""
                    SELECT c.name, COUNT(*) AS cnt
                    FROM listing l
                    JOIN category c ON c.id = l.category_id
                    WHERE l.created_at >= :from AND l.created_at < :toPlus
                    AND l.status = 'ACTIVE'
                    GROUP BY c.name
                """);
        setRange(q, from, to);
        List<Object[]> rows = q.getResultList();
        Map<String, Long> map = new LinkedHashMap<>();
        for (Object[] r : rows) map.put(Objects.toString(r[0], "UNKNOWN"), ((Number) r[1]).longValue());
        return map;
    }

    // 3a) Top brand theo số bài đăng
    public List<Object[]> topBrands(LocalDate from, LocalDate to, int limit) {
        Query q = em.createNativeQuery("""
                    SELECT b.name AS brand_name, COUNT(*) AS cnt
                    FROM listing l
                    JOIN brand b ON b.id = l.brand_id
                    WHERE l.created_at >= :from AND l.created_at < :toPlus
                    AND l.status = 'ACTIVE'
                    GROUP BY b.name
                    ORDER BY cnt DESC
                    LIMIT :lim
                """);
        setRange(q, from, to);
        q.setParameter("lim", limit);
        return q.getResultList();
    }

    // 3b) Top model theo số bài đăng
    public List<Object[]> topModels(LocalDate from, LocalDate to, int limit) {
        Query q = em.createNativeQuery("""
                    SELECT m.name AS model_name, COUNT(*) AS cnt
                    FROM listing l
                    JOIN model m ON m.id = l.model_id
                    WHERE l.created_at >= :from AND l.created_at < :toPlus
                    AND l.status = 'ACTIVE'
                    GROUP BY m.name
                    ORDER BY cnt DESC
                    LIMIT :lim
                """);
        setRange(q, from, to);
        q.setParameter("lim", limit);
        return q.getResultList();
    }

    // 4) Giá trung bình listing theo category (chỉ tính bài có price hợp lệ)
    public Map<String, BigDecimal> avgPriceByCategory(LocalDate from, LocalDate to) {
        Query q = em.createNativeQuery("""
                    SELECT c.name, COALESCE(AVG(l.price),0) AS avg_price
                    FROM listing l
                    JOIN category c ON c.id = l.category_id
                    WHERE l.price IS NOT NULL
                      AND l.created_at >= :from AND l.created_at < :toPlus
                      AND l.status = 'ACTIVE'
                    GROUP BY c.name
                """);
        setRange(q, from, to);
        List<Object[]> rows = q.getResultList();
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        for (Object[] r : rows) {
            String cat = Objects.toString(r[0], "UNKNOWN");
            BigDecimal avg = (r[1] == null) ? BigDecimal.ZERO : (BigDecimal) r[1];
            map.put(cat, avg.setScale(2, java.math.RoundingMode.HALF_UP));
        }
        return map;
    }

    private void setRange(Query q, LocalDate from, LocalDate to) {
        q.setParameter("from", Timestamp.valueOf(from.atStartOfDay()));
        q.setParameter("toPlus", Timestamp.valueOf(to.plusDays(1).atStartOfDay()));
    }

}
