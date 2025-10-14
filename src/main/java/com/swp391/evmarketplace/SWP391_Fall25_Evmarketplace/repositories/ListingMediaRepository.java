package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.listing.ListingMediaDto;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ListingMedia;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.MediaType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ListingMediaRepository extends JpaRepository<ListingMedia, Long> {
    List<ListingMedia> findAllByListingId(Long listingId);

    @Query(value = """
            SELECT m.media_url
            FROM listing_media m
            WHERE m.listing_id = :listingId AND m.media_type = 'IMAGE'
            ORDER BY m.created_at ASC, m.id ASC
            LIMIT 1
            """, nativeQuery = true)
    Optional<String> findThumbnailUrlByListingId(
            @Param("listingId") Long listingId
    );

}
