package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.CategoryCode;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ConsignmentRequestStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ItemType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
    name = "consignment_request",
    indexes = {
        @Index(name = "idx_cr_owner", columnList = "owner_id"),
        @Index(name = "idx_cr_status", columnList = "status"),
        @Index(name = "idx_cr_category", columnList = "category_id")
    }
)
public class ConsignmentRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_cr_owner"))
    private Account owner;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = true,
        foreignKey = @ForeignKey(name = "fk_consignment_staff"))
    private Account staff;

    @Lob
    @Column(name = "rejected_reason", columnDefinition = "text")
    private String rejectedReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false, length = 16)
    private ItemType itemType;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id",
        foreignKey = @ForeignKey(name = "fk_cr_category"))
    private Category category;

    @Column(name = "brand", length = 100, nullable = false)
    private String brand;

    @Column(name = "model", length = 100, nullable = false)
    private String model;

    @Column(name = "year")
    private Integer year;

    @Column(name = "battery_capacity_kwh", precision = 6, scale = 2)
    private BigDecimal batteryCapacityKwh;

    @Column(name = "soh_percent", precision = 5, scale = 2)
    private BigDecimal sohPercent;

    @Column(name = "mileage_km")
    private Integer mileageKm;

    @ManyToOne(optional = false)
    @JoinColumn(name = "preferred_branch_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_cr_branch"))
    private Branch preferredBranch;

    @OneToMany(mappedBy = "request",  fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ConsignmentRequestMedia> mediaList = new ArrayList<>();

    @Column(name = "owner_expected_price", precision = 12, scale = 2)
    private BigDecimal ownerExpectedPrice;

    @Lob
    @Column(name = "note", columnDefinition = "text")
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ConsignmentRequestStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "request")
    private ConsignmentInspection inspection;

    @OneToOne(mappedBy = "request")
    private ConsignmentAgreement agreement;

    public void addMedia(ConsignmentRequestMedia media) {
        media.setRequest(this);
        mediaList.add(media);
    }
}
