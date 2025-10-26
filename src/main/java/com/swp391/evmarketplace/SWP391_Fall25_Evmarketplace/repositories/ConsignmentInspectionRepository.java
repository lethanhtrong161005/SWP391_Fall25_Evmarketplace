package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ConsignmentInspection;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ConsignmentRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ConsignmentInspectionResult;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections.ConsignmentInspectionProjection;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ConsignmentInspectionRepository extends JpaRepository<ConsignmentInspection, Long> {

    Optional<ConsignmentInspection> getInspectionByRequestId(Long requestId);

    boolean existsByRequestIdAndIsActiveTrue(Long requestId);

    // Lấy inspection hiện hành (isActive = true) cho 1 request
    @Query("""
                SELECT 
                    ci.id AS id,
                    ci.request.id AS requestId,
                    ci.branch.id AS branchId,
                    ci.result AS result,
                    ci.inspectionSummary AS inspectionSummary,
                    ci.suggestedPrice AS suggestedPrice,
                    ci.isActive AS isActive,
                    ci.createdAt AS createdAt,
                    ci.updatedAt AS updatedAt
                FROM ConsignmentInspection ci 
                WHERE ci.request.id = :requestId AND ci.isActive = true
            """)
    ConsignmentInspectionProjection findActiveViewByRequestId(@Param("requestId") Long requestId);

    // Lấy tất cả inspection của 1 request (kể cả lịch sử)
//    List<ConsignmentInspection> findAllByRequestIdOrderByCreatedAtDesc(Long requestId);

    // Lọc theo isActive
    @Query("""
                SELECT 
                    ci.id AS id,
                    ci.request.id AS requestId,
                    ci.branch.id AS branchId,
                    ci.result AS result,
                    ci.inspectionSummary AS inspectionSummary,
                    ci.suggestedPrice AS suggestedPrice,
                    ci.isActive AS isActive,
                    ci.createdAt AS createdAt,
                    ci.updatedAt AS updatedAt
                FROM ConsignmentInspection ci 
                WHERE 
                    (:statuses IS NULL OR ci.result IN :statuses)
                            AND (:isActive IS NULL OR ci.isActive = :isActive)
                ORDER BY ci.createdAt DESC
            """)
    List<ConsignmentInspectionProjection> findAllViewsByStatus(
            @Param("statuses") Collection<ConsignmentInspectionResult> statuses,
            @Param("isActive") Boolean isActive);

    @Query("""
                SELECT 
                    ci.id AS id,
                    ci.request.id AS requestId,
                    ci.branch.id AS branchId,
                    ci.result AS result,
                    ci.inspectionSummary AS inspectionSummary,
                    ci.suggestedPrice AS suggestedPrice,
                    ci.isActive AS isActive,
                    ci.createdAt AS createdAt,
                    ci.updatedAt AS updatedAt
                FROM ConsignmentInspection ci 
                WHERE ci.id = :id
            """)
    ConsignmentInspectionProjection findByIdWithRequest(@Param("id") Long id);



//    @EntityGraph(attributePaths = "request")
//    Optional<ConsignmentInspection> findByIdWithRequest(Long id);
}
