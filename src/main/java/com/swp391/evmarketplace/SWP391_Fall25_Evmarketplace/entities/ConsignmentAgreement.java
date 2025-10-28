package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.AgreementDuration;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ConsignmentAgreementStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "consignment_agreement",
        indexes = {
                @Index(name = "idx_cagr_status", columnList = "status")
        })
public class ConsignmentAgreement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "request_id", nullable = false, unique = true,
            foreignKey = @ForeignKey(name = "fk_cagr_req"))
    private ConsignmentRequest request;

    @ManyToOne(optional = false)
    @JoinColumn(name = "staff_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_cagr_staff"))
    private Account staff;

    @Column(name = "media_url", length = 500)
    private String medialUrl;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_cagr_owner"))
    private Account owner;

    @ManyToOne(optional = false)
    @JoinColumn(name = "branch_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_cagr_branch"))
    private Branch branch;

    @Column(name = "commission_percent", precision = 5, scale = 2, nullable = false)
    private BigDecimal commissionPercent;

    @Column(name = "acceptable_price", precision = 12, scale = 2, nullable = false)
    private BigDecimal acceptablePrice;



    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private ConsignmentAgreementStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "duration", nullable = false, length = 16)
    private AgreementDuration duration;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "expire_at")
    private LocalDateTime expireAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "consignmentAgreement")
    @JsonIgnore
    private Listing listing;
}
