package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.SettlementMethod;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.SettlementStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "consignment_settlement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsignmentSettlement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;  // đơn hàng đã hoàn tất (sale_order)

    @Column(name = "agreement_id")
    private Long agreementId; // hợp đồng ký gửi liên quan (có thể null)

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;  // người ký gửi (chủ xe)

    @Column(name = "gross_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal grossAmount; // tổng tiền bán xe

    @Column(name = "commission_percent", precision = 5, scale = 2, nullable = false)
    private BigDecimal commissionPercent; // % hoa hồng

    @Column(name = "commission_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal commissionAmount; // tiền hoa hồng tính ra

    @Column(name = "owner_receive_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal ownerReceiveAmount; // số tiền thực trả cho chủ xe (sau khi trừ hoa hồng)

    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false, length = 20)
    private SettlementMethod method; // BANK_TRANSFER

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SettlementStatus status; // PENDING / PAID / CANCELLED

    @Column(name = "paid_at")
    private LocalDateTime paidAt; // thời gian thực trả tiền (nếu có)

    @Column(name = "note", length = 255)
    private String note;

    @Column(name = "media_url", length = 500)
    private String mediaUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

;


}
