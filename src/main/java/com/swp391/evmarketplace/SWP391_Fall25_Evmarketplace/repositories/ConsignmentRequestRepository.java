package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ConsignmentRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ConsignmentRequestStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections.ConsignmentRequestProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ConsignmentRequestRepository extends JpaRepository<ConsignmentRequest, Long> {
    @Query(value = """
                 select
                   cr.id                   as id,
                   a.phoneNumber           as accountPhone,
                   p.fullName              as accountName,
                   s.id                    as staffId,
                   cr.rejectedReason       as rejectedReason,
                   cr.itemType             as itemType,
                   c.name                  as category,
                   cr.brand                as brand,
                   cr.model                as model,
                   cr.year                 as year,
                   cr.batteryCapacityKwh   as batteryCapacityKwh,
                   cr.sohPercent           as sohPercent,
                   cr.mileageKm            as mileageKm,
                   b.name                  as preferredBranchName,
                   cr.ownerExpectedPrice   as ownerExpectedPrice,
                   cr.status               as status,
                   cr.createdAt            as createdAt
                 from ConsignmentRequest cr
                 join cr.category c
                 join cr.preferredBranch b
                 join cr.owner a
                 left join cr.staff s
                 left join a.profile p
            """,
            countQuery = """
                       select count(cr.id) 
                       from ConsignmentRequest cr
                       join cr.category c
                        join cr.preferredBranch b
                        join cr.owner a
                        left join cr.staff s
                        left join a.profile p
                    """
    )
    Page<ConsignmentRequestProjection> getAll(Pageable pageable);

    //lấy danh sách thuộc về 1 người dùng đã tạo
    @Query(value = """
                 select
                   cr.id                   as id,
                   a.phoneNumber           as accountPhone,
                   p.fullName              as accountName,
                   s.id                    as staffId,
                   cr.rejectedReason       as rejectedReason,
                   cr.itemType             as itemType,
                   c.name                  as category,
                   cr.brand                as brand,
                   cr.model                as model,
                   cr.year                 as year,
                   cr.batteryCapacityKwh   as batteryCapacityKwh,
                   cr.sohPercent           as sohPercent,
                   cr.mileageKm            as mileageKm,
                   b.name                  as preferredBranchName,
                   cr.ownerExpectedPrice   as ownerExpectedPrice,
                   cr.status               as status,
                   cr.createdAt            as createdAt
                 from ConsignmentRequest cr
                 join cr.category c
                 join cr.preferredBranch b
                 join cr.owner a
                 left join cr.staff s
                 left join a.profile p
                 where a.id = :id
            """,
      countQuery = """
        select count(cr.id) from ConsignmentRequest cr join cr.owner a where a.id = :id
        """
    )
    Page<ConsignmentRequestProjection> getAllByOwnerId(@Param("id") Long id, Pageable pageable);

    //lấy danh sách mà 1 staff đảm nhận (đã duyệt và chưa duyệt)
    @Query(value = """
                 select
                   cr.id                   as id,
                   a.phoneNumber           as accountPhone,
                   p.fullName              as accountName,
                   s.id                    as staffId,
                   cr.rejectedReason       as rejectedReason,
                   cr.itemType             as itemType,
                   c.name                  as category,
                   cr.brand                as brand,
                   cr.model                as model,
                   cr.year                 as year,
                   cr.batteryCapacityKwh   as batteryCapacityKwh,
                   cr.sohPercent           as sohPercent,
                   cr.mileageKm            as mileageKm,
                   b.name                  as preferredBranchName,
                   cr.ownerExpectedPrice   as ownerExpectedPrice,
                   cr.status               as status,
                   cr.createdAt            as createdAt
                 from ConsignmentRequest cr
                 join cr.category c
                 join cr.preferredBranch b
                 join cr.owner a
                 join cr.staff s
                 left join a.profile p
                 where s.id = :id
                 and cr.status in :statuses
            """,
            countQuery = """
                    select count(cr.id) from ConsignmentRequest cr
                        join cr.staff s
                        where s.id = :id
                        and cr.status in :statuses
                    """
    )
    Page<ConsignmentRequestProjection> getAllByStaffId(@Param("id") Long id,
                                                       @Param("statuses")Collection<ConsignmentRequestStatus> statuses,
                                                       Pageable pageable);

    //lấy danh sách chưa phân công việc tại cơ sở
    @Query(value = """
                 select
                   cr.id                   as id,
                   a.phoneNumber           as accountPhone,
                   p.fullName              as accountName,
                   s.id                    as staffId,
                   cr.rejectedReason       as rejectedReason,
                   cr.itemType             as itemType,
                   c.name                  as category,
                   cr.brand                as brand,
                   cr.model                as model,
                   cr.year                 as year,
                   cr.batteryCapacityKwh   as batteryCapacityKwh,
                   cr.sohPercent           as sohPercent,
                   cr.mileageKm            as mileageKm,
                   b.name                  as preferredBranchName,
                   cr.ownerExpectedPrice   as ownerExpectedPrice,
                   cr.status               as status,
                   cr.createdAt            as createdAt
                 from ConsignmentRequest cr
                 join cr.category c
                 join cr.preferredBranch b
                 join cr.owner a
                 left join cr.staff s
                 left join a.profile p
                 where b.id = :id
                 and cr.status = SUBMITTED
                 order by cr.createdAt desc, cr.id asc
            """
    )
    List<ConsignmentRequestProjection> getAllByBranchIdAndSubmitted(@Param("id") Long branchId);

    @Query(value = """
                 select
                   cr.id                   as id,
                   a.phoneNumber           as accountPhone,
                   p.fullName              as accountName,
                   s.id                    as staffId,
                   cr.rejectedReason       as rejectedReason,
                   cr.itemType             as itemType,
                   c.name                  as category,
                   cr.brand                as brand,
                   cr.model                as model,
                   cr.year                 as year,
                   cr.batteryCapacityKwh   as batteryCapacityKwh,
                   cr.sohPercent           as sohPercent,
                   cr.mileageKm            as mileageKm,
                   b.name                  as preferredBranchName,
                   cr.ownerExpectedPrice   as ownerExpectedPrice,
                   cr.status               as status,
                   cr.createdAt            as createdAt
                 from ConsignmentRequest cr
                 join cr.category c
                 join cr.preferredBranch b
                 join cr.owner a
                 left join cr.staff s
                 left join a.profile p
                 where b.id = :id
                 and not (cr.status = SUBMITTED)
                 order by cr.createdAt desc, cr.id asc
            """
    )
    Page<ConsignmentRequestProjection> getAllByBranchIdIgnoreSubmitted(@Param("id") Long branchId, Pageable pageable);

  Optional<ConsignmentRequest> findByIdAndOwnerId(Long id, Long ownerId);

}
