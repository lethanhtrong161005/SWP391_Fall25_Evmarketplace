package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ConsignmentRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections.ConsignmentRequestProject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConsignmentRequestRepository extends JpaRepository<ConsignmentRequest, Long> {
    @Query(value = """
                 select
                   cr.id                   as id,
                   a.phoneNumber           as accountPhone,
                   p.fullName              as accountName,
                   cr.itemType             as itemType,
                   c.name                  as category,
                   cr.intendedFor          as intendedFor,
                   cr.brand                as brand,
                   cr.model                as model,
                   cr.year                 as year,
                   cr.batteryCapacityKwh   as batteryCapacityKwh,
                   b.name                  as preferredBranchName,
                   cr.appointmentTime      as appointmentTime,
                   cr.ownerExpectedPrice   as ownerExpectedPrice,
                   cr.status               as status,
                   cr.createdAt            as createdAt
                 from ConsignmentRequest cr
                 join cr. category c
                 join cr.preferredBranch b
                 join cr.owner a
                 left join a.profile p
            """,
                countQuery = """
                        select count(cr.id) from ConsignmentRequest cr
                        """
    )
    Page<ConsignmentRequestProject> getAll(Pageable pageable);

    @Query(value = """
                 select
                   cr.id                   as id,
                   a.phoneNumber           as accountPhone,
                   p.fullName              as accountName,
                   cr.itemType             as itemType,
                   c.name                  as category,
                   cr.intendedFor          as intendedFor,
                   cr.brand                as brand,
                   cr.model                as model,
                   cr.year                 as year,
                   cr.batteryCapacityKwh   as batteryCapacityKwh,
                   b.name                  as preferredBranchName,
                   cr.ownerExpectedPrice   as ownerExpectedPrice,
                   cr.status               as status,
                   cr.createdAt            as createdAt
                 from ConsignmentRequest cr
                 join cr.category c
                 join cr.preferredBranch b
                 join cr.owner a
                 left join a.profile p
                 where a.id = :id
            """,
            countQuery = """
                        select count(cr.id) from ConsignmentRequest cr
                        """
    )
    Page<ConsignmentRequestProject> getAllByID(@Param("id") Long id , Pageable pageable);


}
