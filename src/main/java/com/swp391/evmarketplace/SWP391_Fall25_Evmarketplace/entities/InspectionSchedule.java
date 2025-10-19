package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.InspectionScheduleStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inspection_schedule",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_inspector_day_shift", columnNames = {"inspector_id", "schedule_date", "shift_id"}),
                @UniqueConstraint(name = "uq_schedule_request", columnNames = {"request_id"})
        },
        indexes = {
                @Index(name = "idx_is_request", columnList = "request_id"),
                @Index(name = "idx_is_inspector", columnList = "inspector_id"),
                @Index(name = "idx_is_branch", columnList = "branch_id"),
                @Index(name = "idx_is_date", columnList = "schedule_date"),
                @Index(name = "idx_is_shift", columnList = "shift_id")
        }
)
public class InspectionSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "request_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_is_request"))
    private ConsignmentRequest request;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inspector_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_is_inspector"))
    private Account inspector;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "staff_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_is_staff"))
    private Account staff;

    @ManyToOne(optional = false)
    @JoinColumn(name = "branch_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_is_branch"))
    private Branch branch;

    @ManyToOne(optional = true)
    @JoinColumn(name = "cancelled_by",
            foreignKey = @ForeignKey(name = "fk_is_cancelled_by"))
    private Account cancelledBy;

    @Column(name = "schedule_date", nullable = false)
    private LocalDate scheduleDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "shift_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_is_shift"))
    private ShiftTemplate shift;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private InspectionScheduleStatus status = InspectionScheduleStatus.SCHEDULED;

    @ManyToOne(optional = true)
    @JoinColumn(name = "scheduled_by",
            foreignKey = @ForeignKey(name = "fk_is_scheduled_by"))
    private Account scheduledBy;

    @Column(name = "checkin_at")
    private LocalDateTime checkinAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancelled_reason", columnDefinition = "TEXT")
    private String cancelledReason;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "reschedule_count", nullable = false)
    private int rescheduleCount = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
