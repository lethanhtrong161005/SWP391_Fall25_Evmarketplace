package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "consignment_payout"
)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ConsignmentPayout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "settlement_id", nullable = false)
    private Long settlementId;

    @Column(name = "agreement_id")
    private Long agreementId;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @NotNull
    @Positive
    @Column(name = "paid_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal paidAmount;

    @Column(name = "method", nullable = false, length = 20)
    private String method;

    @Column(name = "note", length = 255)
    private String note;

    @Column(name = "media_url", length = 500)
    private String medialUrl;

    @Column(name = "recorded_by", nullable = false)
    private Long recordedBy;

    @Column(name = "paid_at", nullable = false)
    private LocalDateTime paidAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
