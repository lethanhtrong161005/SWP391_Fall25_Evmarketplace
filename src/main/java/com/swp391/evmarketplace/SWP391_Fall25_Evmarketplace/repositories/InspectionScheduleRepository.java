package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.InspectionSchedule;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.InspectionScheduleStatus;
import jakarta.persistence.LockModeType;
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

    @Query("""
            select s.shift.id as shiftId, count(s.id) as booked
            from InspectionSchedule s
            where s.branch.id = :branchId
              and s.scheduleDate = :date
              and s.shift.itemType = :itemType
              and s.status in :countStatuses
            group by s.shift.id
            """)
    List<Object[]> countBookedByShiftOnDate(@Param("branchId") Long branchId,
                                            @Param("date") LocalDate date,
                                            @Param("itemType") com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ItemType itemType,
                                            @Param("countStatuses") List<InspectionScheduleStatus> countStatuses);

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
