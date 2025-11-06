package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.order.SaleOrderDto;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.OrderStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.MedialUtils;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sale_order",
        indexes = {
                @Index(name = "idx_sale_order_order_code", columnList = "order_code"),
                @Index(name = "idx_sale_order_order_no",       columnList = "order_no"),

        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_sale_order_order_no",   columnNames = "order_no"),
                @UniqueConstraint(name = "uq_sale_order_order_code", columnNames = "order_code")
        }
)

public class SaleOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(optional = false)
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    @ManyToOne
    @JoinColumn(name = "buyer_id", nullable = false)
    private Account buyer;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private Account seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consignment_agreement_id")
    private ConsignmentAgreement consignmentAgreement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @Column(name = "amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 32, nullable = false)
    private OrderStatus status;

    @Column(name = "reserved_until")
    private LocalDateTime reservedUntil;

    @ManyToOne
    @JoinColumn(
            name = "created_by",
            foreignKey =  @ForeignKey(name = "fk_so_created_by")
    )
    private Account createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @JsonIgnore
    @Column(name = "is_open", insertable = false, updatable = false, nullable = true)
    @Generated(GenerationTime.ALWAYS)
    private Boolean isOpen;

    @Column(name = "paid_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @OneToOne(mappedBy = "order")
    private Contract contract;

    @Column(name = "order_code", nullable = false, unique = true)
    @Generated(GenerationTime.INSERT)
    private BigInteger orderCode;

    @Column(name = "order_no", length = 40, nullable = false, unique = true)
    @Generated(GenerationTime.INSERT)
    private String orderNo;

    @Transient
    @JsonProperty("isOpen")
    public boolean isOpenFlag() {
        return Boolean.TRUE.equals(isOpen);
    }


    public SaleOrderDto toDto(SaleOrder order) {
        if (order == null) return null;

        SaleOrderDto dto = new SaleOrderDto();

        dto.setId(order.getId());

        // Listing (required theo mapping của bạn)
        if (order.getListing() != null) {
            dto.setListingId(order.getListing().getId());
            dto.setListingTitle(order.getListing().getTitle());
        }

        // Buyer
        if (order.getBuyer() != null) {
            dto.setBuyerId(order.getBuyer().getId());
            dto.setBuyerName(order.getBuyer().getProfile() != null
                    ? order.getBuyer().getProfile().getFullName() : null);
            dto.setBuyerPhone(order.getBuyer().getPhoneNumber());
        }

        // Seller
        if (order.getSeller() != null) {
            dto.setSellerId(order.getSeller().getId());
            dto.setSellerName(order.getSeller().getProfile() != null
                    ? order.getSeller().getProfile().getFullName() : null);
            dto.setSellerPhone(order.getSeller().getPhoneNumber());
        }


        dto.setOrderNo(order.getOrderNo());
        dto.setOrderCode(order.getOrderCode() != null ? order.getOrderCode() : null);

        dto.setConsignmentAgreementId(order.getConsignmentAgreement() != null
                ? order.getConsignmentAgreement().getId() : null);
        dto.setBranchId(order.getBranch() != null ? order.getBranch().getId() : null);
        dto.setBranchName(order.getBranch() != null ? order.getBranch().getName() : null);

        dto.setAmount(order.getAmount());
        dto.setPaidAmount(order.getPaidAmount());
        dto.setStatus(order.getStatus());
        dto.setReservedUntil(order.getReservedUntil());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        dto.setIsOpen(order.getIsOpen());

        double percent = 0.0;
        if (order.getAmount() != null
                && order.getAmount().compareTo(java.math.BigDecimal.ZERO) > 0
                && order.getPaidAmount() != null) {
            percent = order.getPaidAmount()
                    .multiply(java.math.BigDecimal.valueOf(100))
                    .divide(order.getAmount(), 2, java.math.RoundingMode.HALF_UP)
                    .doubleValue();
        }
        dto.setPaymentProcessPercent(percent);

        dto.setIsReservedActive(order.getReservedUntil() != null
                ? java.time.LocalDateTime.now().isBefore(order.getReservedUntil())
                : null);

        String contractUrl = order.getContract() != null ? order.getContract().getFileUrl() : "";
        dto.setContractUrl(MedialUtils.converMediaNametoMedialUrl(contractUrl, ""));
        dto.setContractId(order.getContract() != null ? order.getContract().getId() : null);
        dto.setContractStatus(order.getContract() != null ? order.getContract().getStatus().name() : null);

        return dto;
    }


}
