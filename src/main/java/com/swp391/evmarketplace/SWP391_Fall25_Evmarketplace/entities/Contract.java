package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.contract.ContractDto;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ContractSignMethod;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ContractStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.MedialUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;


@Entity
@Table(name = "contract")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private SaleOrder order;

    @Column(name = "file_url", nullable = false, length = 500)
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "sign_method", nullable = false, length = 8)
    private ContractSignMethod signMethod = ContractSignMethod.CLICK;

    @Column(name = "sign_log", columnDefinition = "json")
    private String signLog;

    @Column(name = "signed_at")
    private LocalDateTime signedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "effective_from")
    private LocalDateTime effectiveFrom;

    @Column(name = "effective_to")
    private LocalDateTime effectiveTo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ContractStatus status = ContractStatus.UPLOADED;

    public ContractDto toDto(Contract c){
        if (c == null) return null;

        ContractDto dto = new ContractDto();
        dto.setId(c.getId());

        var order = c.getOrder();
        if (order != null) {
            dto.setOrderId(order.getId());
            dto.setOrderNo(order.getOrderNo());
            dto.setOrderCode(order.getOrderCode() != null ? String.valueOf(order.getOrderCode()) : null);

            var listing = order.getListing();
            dto.setListingTitle(listing != null ? listing.getTitle() : null);

            var buyer = order.getBuyer();
            String buyerName = null;
            if (buyer != null) {
                buyerName = (buyer.getProfile() != null && buyer.getProfile().getFullName() != null && !buyer.getProfile().getFullName().isBlank())
                        ? buyer.getProfile().getFullName()
                        : buyer.getPhoneNumber();
            }
            dto.setBuyerName(buyerName);

            var seller = order.getSeller();
            String sellerName = null;
            if (seller != null) {
                sellerName = (seller.getProfile() != null && seller.getProfile().getFullName() != null && !seller.getProfile().getFullName().isBlank())
                        ? seller.getProfile().getFullName()
                        : seller.getPhoneNumber();
            }
            dto.setSellerName(sellerName);

            var branch = order.getBranch();
            dto.setBranchName(branch != null ? branch.getName() : null);
        }

        dto.setFileUrl(MedialUtils.converMediaNametoMedialUrl(c.getFileUrl(), ""));
        dto.setSignMethod(c.getSignMethod());
        dto.setStatus(c.getStatus());

        dto.setCreatedAt(c.getCreatedAt());
        dto.setUpdatedAt(c.getUpdatedAt());
        dto.setSignedAt(c.getSignedAt());
        dto.setEffectiveFrom(c.getEffectiveFrom());
        dto.setEffectiveTo(c.getEffectiveTo());

        return dto;
    }

}
