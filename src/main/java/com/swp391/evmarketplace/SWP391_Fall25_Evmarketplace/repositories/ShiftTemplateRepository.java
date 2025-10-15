package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ShiftTemplate;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShiftTemplateRepository extends JpaRepository<ShiftTemplate, Long> {
    Optional<ShiftTemplate> findByCode(String code);

    List<ShiftTemplate> findByBranch_IdAndItemTypeAndIsActiveTrue(Long branchId, com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ItemType itemType);

    @Query("""
            select st from ShiftTemplate st
            where (:excludeId is null or st.id <> :excludeId)
                and st.isActive = true
                and st.itemType = :itemType
                and ( (:branchId is null and st.branch is null) or (st.branch.id = :branchId) )
                and st.startTime < :endTime
                and st.endTime   > :startTime
            """)
    List<ShiftTemplate> findOverlaps(@Param("branchId") Long branchId,
                                     @Param("itemType") ItemType itemType,
                                     @Param("startTime") LocalTime startTime,
                                     @Param("endTime") LocalTime endTime,
                                     @Param("excludeId") Long excludeId);
}