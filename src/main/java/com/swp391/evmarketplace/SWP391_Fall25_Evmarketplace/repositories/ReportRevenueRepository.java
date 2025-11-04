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
public class ReportRevenueRepository {

    private final EntityManager em;

    // Tổng doanh thu (PAID)
    public BigDecimal sumPaidAmount(LocalDate from, LocalDate to) {
        Query q = em.createNativeQuery("""
                    SELECT COALESCE(SUM(s.amount),0)
                    FROM sale_payment s
                    WHERE s.status = 'PAID'
                      AND s.paid_at >= :from AND s.paid_at < :toPlus
                """);
        setRange(q, from, to);
        Object r = q.getSingleResult();
        return r == null ? BigDecimal.ZERO : (BigDecimal) r;
    }

    // Số payer unique đã thanh toán (PAID)
    public long countDistinctPayingUsers(LocalDate from, LocalDate to) {
        Query q = em.createNativeQuery("""
                    SELECT COUNT(DISTINCT s.payer_id)
                    FROM sale_payment s
                    WHERE s.status = 'PAID'
                      AND s.paid_at >= :from AND s.paid_at < :toPlus
                """);
        setRange(q, from, to);
        return ((Number) q.getSingleResult()).longValue();
    }

    // Doanh thu theo purpose (PAID)
    public Map<String, BigDecimal> sumPaidAmountByPurpose(LocalDate from, LocalDate to) {
        Query q = em.createNativeQuery("""
                    SELECT s.purpose, COALESCE(SUM(s.amount),0)
                    FROM sale_payment s
                    WHERE s.status = 'PAID'
                      AND s.paid_at >= :from AND s.paid_at < :toPlus
                    GROUP BY s.purpose
                """);
        setRange(q, from, to);
        List<Object[]> rows = q.getResultList();
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        for (Object[] r : rows) {
            String purpose = Objects.toString(r[0], "UNKNOWN");
            BigDecimal sum = (r[1] == null) ? BigDecimal.ZERO : (BigDecimal) r[1];
            map.put(purpose, sum);
        }
        return map;
    }

    private void setRange(Query q, LocalDate from, LocalDate to) {
        q.setParameter("from", Timestamp.valueOf(from.atStartOfDay()));
        q.setParameter("toPlus", Timestamp.valueOf(to.plusDays(1).atStartOfDay()));
    }

}
