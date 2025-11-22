package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.SalePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

public interface ReportTransactionRepository extends JpaRepository<SalePayment, Long> {
    // Tổng giao dịch
    @Query(value = """
            SELECT COUNT(*) 
            FROM sale_payment s
            WHERE s.created_at >= :from 
              AND s.created_at < :toPlus
            """, nativeQuery = true)
    Long countTotalCreated(
            @Param("from") Timestamp from,
            @Param("toPlus") Timestamp toPlus
    );

    // Giao dịch thất bại
    @Query(value = """
            SELECT COUNT(*) 
            FROM sale_payment s
            WHERE s.created_at >= :from 
              AND s.created_at < :toPlus
              AND s.status IN ('FAILED')
            """, nativeQuery = true)
    Long countFailedOrRefundedCreated(
            @Param("from") Timestamp from,
            @Param("toPlus") Timestamp toPlus
    );

    // Giao dịch thành công
    @Query(value = """
            SELECT COUNT(*)
            FROM sale_payment s
            WHERE s.status = 'PAID'
              AND s.paid_at >= :from 
              AND s.paid_at < :toPlus
            """, nativeQuery = true)
    Long countPaidSuccess(
            @Param("from") Timestamp from,
            @Param("toPlus") Timestamp toPlus
    );

    // Số lượng giao dịch theo purpose(listing, inspection)
    @Query(value = """
            SELECT s.purpose, COUNT(*)
            FROM sale_payment s
            WHERE s.created_at >= :from 
              AND s.created_at < :toPlus
              AND s.status = 'PAID'
            GROUP BY s.purpose
            """, nativeQuery = true)
    List<Object[]> countByPurposeCreated(
            @Param("from") Timestamp from,
            @Param("toPlus") Timestamp toPlus
    );
}
