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
public class ReportTransactionRepository {

    private final EntityManager em;

    //tổng giao dịch
    public long countTotalCreated(LocalDate from, LocalDate to) {
        Query q = em.createNativeQuery("""
                    SELECT COUNT(*) FROM sale_payment s
                    WHERE s.created_at >= :from AND s.created_at < :toPlus
                """);
        setRange(q, from, to);
        return ((Number) q.getSingleResult()).longValue();
    }

    // số lượng giao dịch thất bại
    public long countFailedOrRefundedCreated(LocalDate from, LocalDate to) {
        Query q = em.createNativeQuery("""
                    SELECT COUNT(*) FROM sale_payment s
                    WHERE s.created_at >= :from AND s.created_at < :toPlus
                      AND s.status IN ('FAILED','REFUNDED')
                """);
        setRange(q, from, to);
        return ((Number) q.getSingleResult()).longValue();
    }

    // số lượng giao dịch thành công
    public long countPaidSuccess(LocalDate from, LocalDate to) {
        Query q = em.createNativeQuery("""
                    SELECT COUNT(*) FROM sale_payment s
                    WHERE s.status='PAID'
                      AND s.paid_at >= :from AND s.paid_at < :toPlus
                """);
        setRange(q, from, to);
        return ((Number) q.getSingleResult()).longValue();
    }

    //số lượng giao dịch theo mục đích
    public Map<String, Long> countByPurposeCreated(LocalDate from, LocalDate to) {
        Query q = em.createNativeQuery("""
                    SELECT s.purpose, COUNT(*) FROM sale_payment s
                    WHERE s.created_at >= :from AND s.created_at < :toPlus
                    GROUP BY s.purpose
                """);
        setRange(q, from, to);
        List<Object[]> rows = q.getResultList();
        Map<String, Long> map = new LinkedHashMap<>();
        for (Object[] r : rows) {
            map.put(Objects.toString(r[0], "UNKNOWN"), ((Number) r[1]).longValue());
        }
        return map;
    }

    private void setRange(Query q, LocalDate from, LocalDate to) {
        q.setParameter("from", Timestamp.valueOf(from.atStartOfDay()));
        q.setParameter("toPlus", Timestamp.valueOf(to.plusDays(1).atStartOfDay()));
    }

}
