package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.SalePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public interface ReportRevenueRepository extends JpaRepository<SalePayment, Long> {
    // Tổng doanh thu (PAID)
    @Query(value = """
            SELECT COALESCE(SUM(s.amount), 0)
            FROM sale_payment s
            WHERE s.status = 'PAID'
              AND s.paid_at >= :from
              AND s.paid_at < :toPlus
            """, nativeQuery = true)
    BigDecimal sumPaidAmount(
            @Param("from") Timestamp from,
            @Param("toPlus") Timestamp toPlus
    );

    // Số payer unique đã thanh toán (PAID)
    @Query(value = """
            SELECT COUNT(DISTINCT s.payer_id)
            FROM sale_payment s
            WHERE s.status = 'PAID'
              AND s.paid_at >= :from
              AND s.paid_at < :toPlus
            """, nativeQuery = true)
    Long countDistinctPayingUsers(
            @Param("from") Timestamp from,
            @Param("toPlus") Timestamp toPlus
    );

    // Doanh thu theo nguồn thu (purpose) (PAID)
    @Query(value = """
            SELECT s.purpose, COALESCE(SUM(s.amount), 0)
            FROM sale_payment s
            WHERE s.status = 'PAID'
              AND s.paid_at >= :from
              AND s.paid_at < :toPlus
            GROUP BY s.purpose
            """, nativeQuery = true)
    List<Object[]> sumPaidAmountByPurpose(
            @Param("from") Timestamp from,
            @Param("toPlus") Timestamp toPlus
    );


}
