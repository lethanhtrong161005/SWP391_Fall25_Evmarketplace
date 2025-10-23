package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.InspectionSchedule;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.InspectionScheduleStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections.InspectionScheduleDetailProjection;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections.StaffScheduleRow;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface InspectionScheduleRepository extends JpaRepository<InspectionSchedule, Long> {

    boolean existsByShift_IdAndStatusIn(Long shiftId, List<InspectionScheduleStatus> statuses);

    boolean existsByStaffIdAndShiftIdAndScheduleDateAndStatusIn(Long staffId,
                                                                Long shiftId,
                                                                LocalDate date,
                                                                Collection<InspectionScheduleStatus> statuses);

    Optional<InspectionSchedule> findFirstByRequest_IdAndStatusInOrderByIdDesc(
            Long requestId, Collection<InspectionScheduleStatus> statuses);

    boolean existsByRequest_IdAndStatusIn(
            Long requestId, Collection<InspectionScheduleStatus> statuses);

    boolean existsByStaff_IdAndShift_IdAndScheduleDateAndStatusIn(
            Long staffId, Long shiftId, LocalDate date,
            Collection<InspectionScheduleStatus> statuses);

    @Lock(LockModeType.PESSIMISTIC_WRITE) // -> SELECT ... FOR UPDATE
    @Query("""
              select s from InspectionSchedule s
              where s.request.id = :requestId
                and s.status in (:activeStatuses)
            """)
    List<InspectionSchedule> lockActives(
            @Param("requestId") Long requestId,
            @Param("activeStatuses") Collection<InspectionScheduleStatus> activeStatuses);


    @Query("""
              select
                s.id as id,
                r.id as requestId,
            
                stf.id as staffId,
                stfProf.fullName as staffName,
            
                b.id as branchId,
                b.name as branchName,
            
                sh.id as shiftId,
                sh.code as shiftCode,
                sh.startTime as shiftStartTime,
                sh.endTime as shiftEndTime,
            
                sbProf.fullName as scheduledByName,
                cbProf.fullName as cancelledByName,
            
                s.scheduleDate as scheduleDate,
                s.status as status,
                s.checkedInAt as checkedInAt,
                s.cancelledAt as cancelledAt,
                s.cancelledReason as cancelledReason,
                s.note as note,
                s.createdAt as createdAt,
                s.updatedAt as updatedAt
              from InspectionSchedule s
                join s.request r
                join s.branch b
                join s.shift sh
                join s.staff stf
                left join stf.profile stfProf
                left join s.scheduledBy sb
                left join sb.profile sbProf
                left join s.cancelledBy cb
                left join cb.profile cbProf
              where r.id = :requestId
              and s.scheduledBy.id = :ownerId
              and s.status in :statuses
            """)
    List<InspectionScheduleDetailProjection> getScheduleDetailByRequestId(
            @Param("requestId") Long requestId,
            @Param("ownerId") Long ownerId,
            @Param("statuses") Collection<InspectionScheduleStatus> statuses
    );


    @Query(value = """
              select
                s.id as id,
                s.scheduleDate as scheduleDate,
                sbProf.fullName as scheduledByName,
                sh.code as shiftCode,
                sh.startTime as shiftStartTime,
                sh.endTime as shiftEndTime,
                s.status as status,
                s.checkedInAt as checkedInAt
              from InspectionSchedule s
                join s.shift sh
                join s.branch b
                join s.scheduledBy sb
                left join sb.profile sbProf
              where s.staff.id = :staffId
                and s.scheduleDate = :date
                and s.status in :statuses
              order by sh.startTime asc
            """
    )
    List<StaffScheduleRow> getListScheduleByDate(
            @Param("staffId") Long staffId,
            @Param("date") LocalDate date,
            @Param("statuses") Collection<InspectionScheduleStatus> statuses);

    //=======================schedule=======================

    //bùng ca
    @Modifying
    @Transactional
    @Query(value = """
            UPDATE inspection_schedule s
            JOIN shift_template st ON st.id = s.shift_id
            JOIN consignment_request cr ON cr.id = s.request_id
            SET s.status = 'NO_SHOW',
                cr.status= 'RESCHEDULED'
            WHERE s.status = 'SCHEDULED'
                AND s.checkin_at IS NULL
                AND TIMESTAMP(s.schedule_date, st.end_time) < (NOW() - INTERVAL :grace MINUTE)
            """, nativeQuery = true)
    int markNoShow(@Param("grace") int graceMinutes);


}
