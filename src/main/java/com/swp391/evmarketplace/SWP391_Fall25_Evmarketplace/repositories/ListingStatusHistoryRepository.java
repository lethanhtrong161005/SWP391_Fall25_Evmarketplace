package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.listing.ListingHistoryDto;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ListingStatusHistory;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ListingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface ListingStatusHistoryRepository extends JpaRepository<ListingStatusHistory, Long> {
    @Query("""
    select new com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.listing.ListingHistoryDto(
        h.id,
        l.id,
        l.title,
        h.fromStatus,
        h.toStatus,
        h.reason,
        h.note,
        h.createdAt,
        l.category.name,
        l.price,
        l.visibility,
        l.province,
        a.id,
        p.fullName
    )
    from ListingStatusHistory h
    join h.listing l
    left join h.actor a
    left join a.profile p
    where (:actorId is null or a.id = :actorId)
      and (:fromTs is null or h.createdAt >= :fromTs)
      and (:toTs   is null or h.createdAt <  :toTs)
      and (:q is null or lower(l.title) like lower(concat('%', :q, '%')))
      and (:reasonsEmpty = true or h.reason in :reasons)
      and (:toEmpty = true or h.toStatus in :toStatuses)
    order by h.createdAt desc
    """)
    Page<ListingHistoryDto> findModeratorHistory(
            @Param("actorId") Long actorId,
            @Param("q") String q,
            @Param("fromTs") LocalDateTime fromTs,
            @Param("toTs") LocalDateTime toTs,
            @Param("reasonsEmpty") boolean reasonsEmpty,
            @Param("reasons") List<String> reasons,
            @Param("toEmpty") boolean toEmpty,
            @Param("toStatuses") Set<ListingStatus> toStatuses,
            Pageable pageable
    );
}
