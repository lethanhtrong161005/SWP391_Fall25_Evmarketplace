package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ContractSignMethod;
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

}
