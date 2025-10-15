package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ItemType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "shift_template",
	indexes = {
		@Index(name = "idx_shift_branch_item", columnList = "branch_id,item_type")
	},
	uniqueConstraints = {
		@UniqueConstraint(name = "uk_shift_template_code", columnNames = {"code"})
	}
)
public class ShiftTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", length = 32, nullable = false, unique = true)
    private String code;

    @Column(name = "name", length = 64, nullable = false)
    private String name; // "Ca sáng (xe)", "Ca chiều (pin)"

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", length = 16, nullable = false)
    private ItemType itemType; // VEHICLE or BATTERY

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id",
	    foreignKey = @ForeignKey(name = "fk_shift_branch"))
    private Branch branch; // nullable per schema

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = Boolean.TRUE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
