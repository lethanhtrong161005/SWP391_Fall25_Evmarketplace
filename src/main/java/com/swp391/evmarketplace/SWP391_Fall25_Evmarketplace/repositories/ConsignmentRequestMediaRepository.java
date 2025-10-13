package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ConsignmentRequestMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsignmentRequestMediaRepository extends JpaRepository<ConsignmentRequestMedia, Long> {
    @Query("select m.mediaUrl from ConsignmentRequestMedia m where m.request.id = :requestId")
    List<String> findAllMediaUrlsByRequestId(@Param("requestId") Long requestId);

    @Query("select m.request.id, m.mediaUrl from ConsignmentRequestMedia m where m.request.id in :requestIds")
    List<Object[]> findAllMediaUrlsByRequestIds(@Param("requestIds") List<Long> requestIds);
}
