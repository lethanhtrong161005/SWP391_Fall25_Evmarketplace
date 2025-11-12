package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.consignment.request.ConsignmentRequestListItemDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ConsignmentRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ConsignmentRequestStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections.ConsignmentRequestProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

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
                   
                   c.id                    as categoryId,
                   c.name                  as category,
                   
                   cr.brandId              as brandId,
                   cr.brand                as brand,
                   
                   cr.modelId              as modelId,
                   cr.model                as model,
                   
                   cr.year                 as year,
                   cr.batteryCapacityKwh   as batteryCapacityKwh,
                   cr.sohPercent           as sohPercent,
                   cr.mileageKm            as mileageKm,
                   
                   b.id                    as preferredBranchId,
                   b.name                  as preferredBranchName,
                   cr.ownerExpectedPrice   as ownerExpectedPrice,
                   cr.status               as status,
                   cr.createdAt            as createdAt,
                   cb.id                   as cancelledById,
                   cr.cancelledAt          as cancelledAt,
                   cr.cancelledReason      as cancelledReason
                 from ConsignmentRequest cr
                 join cr.category c
                 join cr.preferredBranch b
                 join cr.owner a
                 left join cr.staff s
                 left join a.profile p
                 left join cr.cancelledBy cb
            """,
            countQuery = """
                       select count(cr.id)
                       from ConsignmentRequest cr
                       join cr.category c
                        join cr.preferredBranch b
                        join cr.owner a
                        left join cr.staff s
                        left join a.profile p
                        left join cr.cancelledBy cb
                    
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
                   
                   c.id                    as categoryId,
                   c.name                  as category,
                   
                   cr.brandId              as brandId,
                   cr.brand                as brand,
                   
                   cr.modelId              as modelId,
                   cr.model                as model,
                   
                   cr.year                 as year,
                   cr.batteryCapacityKwh   as batteryCapacityKwh,
                   cr.sohPercent           as sohPercent,
                   cr.mileageKm            as mileageKm,
                   
                   b.id                    as preferredBranchId,
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
                   
                   c.id                    as categoryId,
                   c.name                  as category,
                   
                   cr.brandId              as brandId,
                   cr.brand                as brand,
                   
                   cr.modelId              as modelId,
                   cr.model                as model,
                   
                   cr.year                 as year,
                   cr.batteryCapacityKwh   as batteryCapacityKwh,
                   cr.sohPercent           as sohPercent,
                   cr.mileageKm            as mileageKm,
                   
                   b.id                    as preferredBranchId,
                   b.name                  as preferredBranchName,
                   cr.ownerExpectedPrice   as ownerExpectedPrice,
                   cr.status               as status,
                   cr.createdAt            as createdAt,
                   cb.id                   as cancelledById,
                   cr.cancelledAt          as cancelledAt,
                   cr.cancelledReason      as cancelledReason
                 from ConsignmentRequest cr
                 join cr.category c
                 join cr.preferredBranch b
                 join cr.owner a
                 join cr.staff s
                 left join a.profile p
                 left join cr.cancelledBy cb
                 where s.id = :id
                 and cr.status in :statuses
            """,
            countQuery = """
                       select count(cr.id) from ConsignmentRequest cr
                           join cr.category c
                            join cr.preferredBranch b
                            join cr.owner a
                            join cr.staff s
                            left join a.profile p
                            left join cr.cancelledBy cb
                           where s.id = :id
                           and cr.status in :statuses
                    """
    )
    Page<ConsignmentRequestProjection> getAllByStaffId(@Param("id") Long id,
                                                       @Param("statuses") Collection<ConsignmentRequestStatus> statuses,
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
                   cr.createdAt            as createdAt,
                   cb.id                   as cancelledById,
                   cr.cancelledAt          as cancelledAt,
                   cr.cancelledReason      as cancelledReason
                 from ConsignmentRequest cr
                 join cr.category c
                 join cr.preferredBranch b
                 join cr.owner a
                 left join cr.staff s
                 left join a.profile p
                 left join cr.cancelledBy cb
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
                   
                   c.id                    as categoryId,
                   c.name                  as category,
                   
                   cr.brandId              as brandId,
                   cr.brand                as brand,
                   
                   cr.modelId              as modelId,
                   cr.model                as model,
                   
                   cr.year                 as year,
                   cr.batteryCapacityKwh   as batteryCapacityKwh,
                   cr.sohPercent           as sohPercent,
                   cr.mileageKm            as mileageKm,
                   
                   b.id                    as preferredBranchId,
                   b.name                  as preferredBranchName,
                   cr.ownerExpectedPrice   as ownerExpectedPrice,
                   cr.status               as status,
                   cr.createdAt            as createdAt,
                   cb.id                   as cancelledById,
                   cr.cancelledAt          as cancelledAt,
                   cr.cancelledReason      as cancelledReason
                 from ConsignmentRequest cr
                 join cr.category c
                 join cr.preferredBranch b
                 join cr.owner a
                 left join cr.staff s
                 left join a.profile p
                 left join cr.cancelledBy cb
                 where b.id = :id
                 and not (cr.status = SUBMITTED)
                 order by cr.createdAt desc, cr.id asc
            """
    )
    Page<ConsignmentRequestProjection> getAllByBranchIdIgnoreSubmitted(@Param("id") Long branchId, Pageable pageable);

    @Query(value = """
                 select
                   cr.id                   as id,
                   a.phoneNumber           as accountPhone,
                   p.fullName              as accountName,
                   s.id                    as staffId,
                   cr.rejectedReason       as rejectedReason,
                   cr.itemType             as itemType,
                   
                   c.id                    as categoryId,
                   c.name                  as category,
                   
                   cr.brandId              as brandId,
                   cr.brand                as brand,
                   
                   cr.modelId              as modelId,
                   cr.model                as model,
                   
                   cr.year                 as year,
                   cr.batteryCapacityKwh   as batteryCapacityKwh,
                   cr.sohPercent           as sohPercent,
                   cr.mileageKm            as mileageKm,
                   
                   b.id                    as preferredBranchId,
                   b.name                  as preferredBranchName,
                   cr.ownerExpectedPrice   as ownerExpectedPrice,
                   cr.status               as status,
                   cr.createdAt            as createdAt,
                   cb.id                   as cancelledById,
                   cr.cancelledAt          as cancelledAt,
                   cr.cancelledReason      as cancelledReason
                 from ConsignmentRequest cr
                 join cr.category c
                 join cr.preferredBranch b
                 join cr.owner a
                 left join cr.staff s
                 left join a.profile p
                 left join cr.cancelledBy cb
                 where cr.id = :id
            """
    )
    Optional<ConsignmentRequestProjection> getRequestById(@Param("id") Long id);

    Optional<ConsignmentRequest> findByIdAndOwnerId(Long id, Long ownerId);

    @Query(value = """
                 select
                   cr.id                   as id,
                   a.phoneNumber           as accountPhone,
                   p.fullName              as accountName,
                   s.id                    as staffId,
                   cr.rejectedReason       as rejectedReason,
                   cr.itemType             as itemType,
                   
                   c.id                    as categoryId,
                   c.name                  as category,
                   
                   cr.brandId              as brandId,
                   cr.brand                as brand,
                   
                   cr.modelId              as modelId,
                   cr.model                as model,
                   
                   cr.year                 as year,
                   cr.batteryCapacityKwh   as batteryCapacityKwh,
                   cr.sohPercent           as sohPercent,
                   cr.mileageKm            as mileageKm,
                   
                   b.id                    as preferredBranchId,
                   b.name                  as preferredBranchName,
                   cr.ownerExpectedPrice   as ownerExpectedPrice,
                   cr.status               as status,
                   cr.createdAt            as createdAt,
                   cb.id                   as cancelledById,
                   cr.cancelledAt          as cancelledAt,
                   cr.cancelledReason      as cancelledReason
                 from ConsignmentRequest cr
                 join cr.category c
                 join cr.preferredBranch b
                 join cr.owner a
                 left join cr.staff s
                 left join a.profile p
                 left join cr.cancelledBy cb
                 where a.phoneNumber = :phone
            """
    )
    List<ConsignmentRequestProjection> searchByPhone(@Param("phone") String phone);



    //=======================schedule=======================
    //ngâm request quá 7 ngày không đặt lịch -> EXPIRED
    //SCHEDULING, REJECT, RESCHEDULE
    @Modifying
    @Transactional
    @Query(value = """
            UPDATE consignment_request cr
            SET cr.status = 'EXPIRED',
                cr.status_changed_at = NOW()
            WHERE cr.status in :statuses
                AND cr.status_changed_at < (NOW() - INTERVAL 7 DAY)
            """, nativeQuery = true)
    int expiredRequest(
            @Param("statuses") Collection<String> statuses);
}
