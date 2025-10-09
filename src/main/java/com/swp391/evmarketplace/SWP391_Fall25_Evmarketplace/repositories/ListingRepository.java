package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.listing.SearchListingRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ListingStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections.ListingListProjection;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections.ListingStatusCount;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Listing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {
    Optional<Listing> findById(long id);


    //SpEL :#{#req.field} cho phép bạn tham chiếu trực tiếp vào field của DTO.
    @Query("""
              select
                    l.id as id,
                    l.title as title,
                    l.brand as brand,
                    l.model as model,
                    l.year as year,
                    p.fullName as sellerName,
                    l.price as price,
                    l.province as province,
                    l.batteryCapacityKwh as batteryCapacityKwh,
                    l.sohPercent as sohPercent,
                    l.mileageKm as mileageKm,
                    l.createdAt as createdAt,
                    l.status as status,
                    l.visibility as visibility,
                    l.consigned as isConsigned
                from Listing l
                join l.seller a
                join a.profile p
              where l.status in :statuses
                and (
                          :#{#req.key} is null
                          or lower(l.brand) like lower(concat('%', :#{#req.key}, '%'))
                          or lower(l.model) like lower(concat('%', :#{#req.key}, '%'))
                          or lower(l.title) like lower(concat('%', :#{#req.key}, '%'))
                        )
                and (:#{#req.yearFrom} is null or l.year >= :#{#req.yearFrom})
                and (:#{#req.yearTo}   is null or l.year <= :#{#req.yearTo})
                and (:#{#req.capacityMin} is null or l.batteryCapacityKwh >= :#{#req.capacityMin})
                and (:#{#req.capacityMax} is null or l.batteryCapacityKwh <= :#{#req.capacityMax})
                and (:#{#req.priceMin}    is null or l.price >= :#{#req.priceMin})
                and (:#{#req.priceMax}    is null or l.price <= :#{#req.priceMax})
                and (:#{#req.mileageMin}  is null or l.mileageKm >= :#{#req.mileageMin})
                and (:#{#req.mileageMax}  is null or l.mileageKm <= :#{#req.mileageMax})
                and (:#{#req.sohMin}      is null or l.sohPercent >= :#{#req.sohMin})
                and (:#{#req.sohMax}      is null or l.sohPercent <= :#{#req.sohMax})
            """)
    Slice<ListingListProjection> searchCards(
            @Param("req") SearchListingRequestDTO req,
            @Param("statuses") Collection<ListingStatus> statuses,
            Pageable pageable);

    @Query("""
            select
                    l.id as id,
                    l.title as title,
                    l.brand as brand,
                    l.model as model,
                    l.year as year,
                    p.fullName as sellerName,
                    l.price as price,
                    l.province as province,
                    l.batteryCapacityKwh as batteryCapacityKwh,
                    l.sohPercent as sohPercent,
                    l.mileageKm as mileageKm,
                    l.createdAt as createdAt,
                    l.status as status,
                    l.visibility as visibility,
                    l.consigned as isConsigned
                from Listing l
                join l.seller a
                join a.profile p
            where l.status in :statuses
            """)
    Slice<ListingListProjection> getAllList(
            @Param("statuses") Collection<ListingStatus> statuses,
            Pageable pageable);

    @Query(
            value = """
                      select
                        l.id as id,
                        l.title as title,
                        l.brand as brand,
                        l.model as model,
                        l.year as year,
                        l.price as price,
                        l.province as province,
                        l.batteryCapacityKwh as batteryCapacityKwh,
                        l.sohPercent as sohPercent,
                        l.mileageKm as mileageKm,
                        l.createdAt as createdAt,
                        l.status as status,
                        l.visibility as visibility,
                        l.consigned as isConsigned
                      from Listing l
                      join l.seller a
                      where a.id = :sellerId
                    """,
            countQuery = """
                      select count(l)
                      from Listing l
                      join l.seller a
                      where a.id = :sellerId
                    """
    )
    Page<ListingListProjection> findBySeller(
            @Param("sellerId") Long sellerId,
            Pageable pageable);

    @Query(
            value = """
                select
                  l.id as id,
                  l.title as title,
                  l.brand as brand,
                  l.model as model,
                  l.year as year,
                  l.price as price,
                  l.province as province,
                  l.batteryCapacityKwh as batteryCapacityKwh,
                  l.sohPercent as sohPercent,
                  l.mileageKm as mileageKm,
                  l.createdAt as createdAt,
                  l.status as status,
                  l.visibility as visibility,
                  l.consigned as isConsigned,
                  p.fullName as sellerName
                from Listing l
                join l.seller s
                left join s.profile p
                where s.id = :sellerId
                  and (:status is null or l.status = :status)
                  and (
                       :q is null
                       or lower(l.title) like lower(concat('%', :q, '%'))
                       or lower(l.brand) like lower(concat('%', :q, '%'))
                       or lower(l.model) like lower(concat('%', :q, '%'))
                  )
                """,
            countQuery = """
                select count(l)
                from Listing l
                join l.seller s
                where s.id = :sellerId
                  and (:status is null or l.status = :status)
                  and (
                       :q is null
                       or lower(l.title) like lower(concat('%', :q, '%'))
                       or lower(l.brand) like lower(concat('%', :q, '%'))
                       or lower(l.model) like lower(concat('%', :q, '%'))
                  )
                """
    )
    Page<ListingListProjection> findMine(
            @Param("sellerId") Long sellerId,
            @Param("status") ListingStatus status,
            @Param("q") String q,
            Pageable pageable
    );

    @Query("""
       select l.status as status, count(l) as total
       from Listing l
       where l.seller.id = :sellerId
       group by l.status
       """)
    List<ListingStatusCount> countBySellerGroupedStatus(@Param("sellerId") Long sellerId);
}
