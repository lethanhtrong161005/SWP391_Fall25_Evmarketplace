package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByAccount_IdAndListing_Id(Long accountId, Long listingId);
    int deleteByAccount_IdAndListing_Id(Long accountId, Long listingId);
    Page<Favorite> findByAccount_Id(Long accountId, Pageable pageable);
    long countByAccount_Id(Long accountId);
}

