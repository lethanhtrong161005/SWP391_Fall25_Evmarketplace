package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.InspectionSchedule;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ShiftTemplate;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.InspectionScheduleStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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

//
//    @Query("""
//              select s
//              from ShiftTemplate s
//              where s.isActive = true
//                and (:branchId is null or s.branch.id = :branchId)
//                and (:type     is null or s.itemType   = :type)
//                and not exists (
//                  select i.id
//                  from InspectionSchedule i
//                  where i.shift.id      = s.id
//                    and i.scheduleDate  = :date
//                    and i.staff.id      = :staffId
//                    and i.status in :statuses
//                )
//              order by s.startTime asc
//            """)
//    List<ShiftTemplate> findAvailableShift(@Param("staffId") Long staffId,
//                                           @Param("date") LocalDate date,
//                                           @Param("branchId") Long branchId,
//                                           @Param("type") ItemType type);

    boolean existsByStaffIdAndShiftIdAndScheduleDateAndStatusIn(Long staffId,
                                                                Long shiftId,
                                                                LocalDate date,
                                                                Collection<InspectionScheduleStatus> statuses);

    Optional<InspectionSchedule> findFirstByRequest_IdAndStatusInOrderByIdDesc(
            Long requestId, Collection<InspectionScheduleStatus> statuses);

    boolean existsByStaff_IdAndShift_IdAndScheduleDateAndStatusIn(
            Long staffId, Long shiftId, LocalDate date,
            Collection<InspectionScheduleStatus> statuses);


}
