package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Favorite;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections.ListingLikeCount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByAccount_IdAndListing_Id(Long accountId, Long listingId);
    int deleteByAccount_IdAndListing_Id(Long accountId, Long listingId);
    Page<Favorite> findByAccount_Id(Long accountId, Pageable pageable);
    long countByAccount_Id(Long accountId);

    long countByListing_Id(Long listingId);

    Slice<Favorite> findByAccount_IdOrderByCreatedAtDesc(Long accountId, Pageable pageable);

    @Query("""
           select f.listing.id as listingId, count(f.id) as cnt
           from Favorite f
           where f.listing.id in :listingIds
           group by f.listing.id
           """)
    List<ListingLikeCount> countByListingIds(@Param("listingIds") Collection<Long> listingIds);

    @Query("""
           select f.listing.id
           from Favorite f
           where f.account.id = :accountId
             and f.listing.id in :listingIds
           """)
    Set<Long> findListingIdsLikedByAccount(@Param("accountId") Long accountId,
                                           @Param("listingIds") Collection<Long> listingIds);
}

