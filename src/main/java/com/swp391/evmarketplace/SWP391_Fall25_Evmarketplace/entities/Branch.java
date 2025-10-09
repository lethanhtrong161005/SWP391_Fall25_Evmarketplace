package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.branch.BranchDto;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.BranchStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "branch",
        indexes = {
                @Index(name = "idx_branch_province", columnList = "province"),
                @Index(name = "idx_branch_status",   columnList = "status")
        }
)
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;              // Tên cơ sở

    @Column(nullable = false, length = 100)
    private String province;          // Tỉnh/thành

    @Column(nullable = false, length = 255)
    private String address;           // Địa chỉ chi tiết

    @Column(length = 20)
    private String phone;             // SĐT liên hệ

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private BranchStatus status = BranchStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "manager_id",
            foreignKey = @ForeignKey(name = "fk_branch_manager"))
    private Account manager;

    public BranchDto toDto(Branch branch) {
        BranchDto dto = new BranchDto();
        dto.setId(branch.getId());
        dto.setProvince(branch.getProvince());
        dto.setAddress(branch.getAddress());
        dto.setPhone(branch.getPhone());
        dto.setName(branch.getName());
        dto.setStatus(branch.getStatus().name());
        return dto;
    }

}
