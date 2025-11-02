package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReportTransactionRepository {

    private final EntityManager em;
//
//    public long countTotalCreated(LocalDate from, LocalDate to) {
//        Query q = em.createNativeQuery("""
//                    SELECT COUNT(*) FROM transaction t
//                    WHERE t.created_at >= :from AND t.created_at < :toPlus
//                """);
//        setRange(q, from, to);
//        return ((Number) q.getSingleResult()).longValue();
//    }
//
//    public long countFailedOrRefundedCreated(LocalDate from, LocalDate to) {
//        Query q = em.createNativeQuery("""
//                    SELECT COUNT(*) FROM transaction t
//                    WHERE t.created_at >= :from AND t.created_at < :toPlus
//                      AND t.status IN ('FAILED','REFUNDED')
//                """);
//        setRange(q, from, to);
//        return ((Number) q.getSingleResult()).longValue();
//    }
//
//    public long countPaidSuccess(LocalDate from, LocalDate to) {
//        Query q = em.createNativeQuery("""
//                    SELECT COUNT(*) FROM transaction t
//                    WHERE t.status='PAID'
//                      AND t.paid_at >= :from AND t.paid_at < :toPlus
//                """);
//        setRange(q, from, to);
//        return ((Number) q.getSingleResult()).longValue();
//    }
//
//    public Map<String, Long> countByPurposeCreated(LocalDate from, LocalDate to) {
//        Query q = em.createNativeQuery("""
//                    SELECT t.purpose, COUNT(*) FROM transaction t
//                    WHERE t.created_at >= :from AND t.created_at < :toPlus
//                    GROUP BY t.purpose
//                """);
//        setRange(q, from, to);
//        List<Object[]> rows = q.getResultList();
//        Map<String, Long> map = new LinkedHashMap<>();
//        for (Object[] r : rows) {
//            map.put(Objects.toString(r[0], "UNKNOWN"), ((Number) r[1]).longValue());
//        }
//        return map;
//    }
//
//    public BigDecimal sumPaidAmount(LocalDate from, LocalDate to) {
//        Query q = em.createNativeQuery("""
//                    SELECT COALESCE(SUM(t.amount),0) FROM transaction t
//                    WHERE t.status='PAID'
//                      AND t.paid_at >= :from AND t.paid_at < :toPlus
//                """);
//        setRange(q, from, to);
//        Object r = q.getSingleResult();
//        return r == null ? BigDecimal.ZERO : (BigDecimal) r;
//    }
//
//    public Map<String, Long> countSoldConsignmentByCategory(LocalDate from, LocalDate to) {
//        Query q = em.createNativeQuery("""
//                    SELECT ci.category, COUNT(*) FROM consignment_item ci
//                    WHERE ci.status IN ('SOLD','FINISHED')
//                      AND ci.sold_at >= :from AND ci.sold_at < :toPlus
//                    GROUP BY ci.category
//                """);
//        setRange(q, from, to);
//        List<Object[]> rows = q.getResultList();
//        Map<String, Long> map = new LinkedHashMap<>();
//        for (Object[] r : rows) {
//            map.put(Objects.toString(r[0], "UNKNOWN"), ((Number) r[1]).longValue());
//        }
//        return map;
//    }
//
//    private void setRange(Query q, LocalDate from, LocalDate to) {
//        q.setParameter("from", Timestamp.valueOf(from.atStartOfDay()));
//        q.setParameter("toPlus", Timestamp.valueOf(to.plusDays(1).atStartOfDay()));
//    }

}
