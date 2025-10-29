package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ConsignmentAgreement;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections.ConsignmentAgreementProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ConsignmentAgreementRepository extends JpaRepository<ConsignmentAgreement, Long> {

    boolean existsByRequestId(Long requestId);

    @Query("""
                SELECT
                    ca.id AS id,
                    ca.request.id AS requestId,
                    ca.owner.id AS ownerId,
                    s.id AS staffId,
                    ca.branch.id AS branchId,
                    ca.commissionPercent AS commissionPercent,
                    ca.acceptablePrice AS acceptablePrice,
                    ca.status AS status,
                    ca.duration AS duration,
                    ca.medialUrl as medialUrl,
                    ca.startAt AS startAt,
                    ca.expireAt AS expireAt,
                    ca.createdAt AS createdAt,
                    ca.updatedAt AS updatedAt
                FROM ConsignmentAgreement ca
                join ca.staff s
                WHERE ca.request.id = :requestId
            """)
    Optional<ConsignmentAgreementProjection> findProjectionByRequestId(Long requestId);

    @Query("""
                SELECT
                    ca.id AS id,
                    ca.request.id AS requestId,
                    ca.owner.id AS ownerId,
                    s.id AS staffId,
                    ca.branch.id AS branchId,
                    ca.commissionPercent AS commissionPercent,
                    ca.acceptablePrice AS acceptablePrice,
                    ca.status AS status,
                    ca.duration AS duration,
                    ca.medialUrl as medialUrl,
                    ca.startAt AS startAt,
                    ca.expireAt AS expireAt,
                    ca.createdAt AS createdAt,
                    ca.updatedAt AS updatedAt
                FROM ConsignmentAgreement ca
                join ca.staff s
                ORDER BY ca.createdAt DESC
            """)
    List<ConsignmentAgreementProjection> findAllProjections();


    //    scheduler
    @Query("""
                SELECT ca
                FROM ConsignmentAgreement ca
                JOIN ca.request cr
                WHERE ca.status = 'SIGNED'
                  AND ca.expireAt IS NOT NULL
                  AND ca.expireAt < CURRENT_TIMESTAMP
            """)
    List<ConsignmentAgreement> findExpiredAgreements();

}


