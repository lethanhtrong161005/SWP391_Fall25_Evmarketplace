package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.listing.SearchListingRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.listing.ListingListProjection;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.listing.SearchListingResponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Listing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {

    @Query(value = "SELECT * FROM listing", nativeQuery = true)
    List<Listing> findAll();

    Optional<Listing> findById(long id);

    //JPQL
    //SpEL :#{#req.field} cho phép bạn tham chiếu trực tiếp vào field của DTO.
    @Query("""
              select new com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.listing.SearchListingResponseDTO(
                l.id,
                l.title, l.brand, l.model,
                l.province, l.city,
                l.year, l.mileageKm,
                l.batteryCapacityKwh, l.sohPercent, l.price,
                l.createdAt
              )
              from Listing l
              where l.status = 'ACTIVE' 
                and (:#{#req.brand} is null or lower(l.brand) = lower(:#{#req.brand}))
                and (:#{#req.modelKeyword} is null or lower(l.model) like lower(concat('%', :#{#req.modelKeyword}, '%')))
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
    Slice<SearchListingResponseDTO> searchCards(@Param("req") SearchListingRequestDTO req,
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
                    l.createdAt as createdAt,
                    l.status as status
                from Listing l
                join l.seller a
                join a.profile p
            """)
    Slice<ListingListProjection> getAllList(Pageable pageable);

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
                        l.createdAt as createdAt,
                        l.status as status
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
    Page<ListingListProjection> findBySeller(@Param("sellerId") Long sellerId, Pageable pageable);


}
