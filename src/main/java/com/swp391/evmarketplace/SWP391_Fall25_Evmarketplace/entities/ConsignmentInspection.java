package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ConsignmentInspectionResult;
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
@Table(name = "consignment_inspection")
public class ConsignmentInspection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "request_id", nullable = false, unique = true,
            foreignKey = @ForeignKey(name = "fk_ci_req"))
    private ConsignmentRequest request;

    @ManyToOne(optional = false)
    @JoinColumn(name = "branch_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_ci_branch"))
    private Branch branch;

    @ManyToOne(optional = false)
    @JoinColumn(name = "inspector_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_ci_inspector"))
    private Account inspector;

//    @Column(name = "inspection_time", nullable = false)
//    private LocalDateTime inspectionTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "result", nullable = false, length = 16)
    private ConsignmentInspectionResult result;

    @Lob
    @Column(name = "defect_summary", columnDefinition = "text")
    private String defectSummary;

    @Column(name = "suggested_price", precision = 12, scale = 2)
    private BigDecimal suggestedPrice;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
