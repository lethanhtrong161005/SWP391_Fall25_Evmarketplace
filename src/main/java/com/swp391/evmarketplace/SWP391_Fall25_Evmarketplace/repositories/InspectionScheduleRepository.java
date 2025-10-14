package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.InspectionSchedule;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.InspectionScheduleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

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
}
