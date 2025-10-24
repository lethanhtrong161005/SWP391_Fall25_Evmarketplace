package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.PaymentMethod;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.PaymentPurpose;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sale_payment")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SalePayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", foreignKey = @ForeignKey(name = "fk_pay_order"))
    private SaleOrder order;

    @ManyToOne
    @JoinColumn(name="listing_id")
    private Listing listing;

    @ManyToOne(optional = false)
    @JoinColumn(name="payer_id", nullable = false)
    private Account payer;

    @Column(name="amount", precision=12, scale=2, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name="method", nullable = false, length = 10)
    private PaymentMethod method = PaymentMethod.VNPAY;

    @Enumerated(EnumType.STRING)
    @Column(name="purpose", nullable=false, length=16)
    private PaymentPurpose purpose = PaymentPurpose.ORDER;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable = false, length = 12)
    private PaymentStatus status = PaymentStatus.INIT;

    @Column(name="provider_txn_id", unique = true)
    private String providerTxnId;

    @Column(name="paid_at") private LocalDateTime paidAt;

    @CreationTimestamp
    @Column(name="created_at", updatable=false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "reference_no", length = 64)
    private String referenceNo;

    @ManyToOne
    @JoinColumn(name = "recorded_by",
            foreignKey = @ForeignKey(name = "fk_pay_recorded_by"))
    private Account recordedBy;

    @Column(name = "note", length = 255)
    private String note;


}
