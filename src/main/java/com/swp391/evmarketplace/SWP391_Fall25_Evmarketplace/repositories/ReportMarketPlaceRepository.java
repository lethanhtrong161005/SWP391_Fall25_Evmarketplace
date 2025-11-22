package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;

public interface ReportMarketPlaceRepository extends JpaRepository<Listing, Long> {

    @Query(value = """
        SELECT l.visibility, COUNT(*) AS cnt
        FROM listing l
        WHERE l.created_at >= :from
          AND l.created_at < :toPlus
          AND l.status = 'ACTIVE'
        GROUP BY visibility
    """, nativeQuery = true)
    List<Object[]> countPostType(Timestamp from, Timestamp toPlus);

    @Query(value = """
        SELECT c.name, COUNT(*) AS cnt
        FROM listing l
        JOIN category c ON c.id = l.category_id
        WHERE l.created_at >= :from 
          AND l.created_at < :toPlus
          AND l.status = 'ACTIVE'
        GROUP BY c.name
    """, nativeQuery = true)
    List<Object[]> countByCategory(Timestamp from, Timestamp toPlus);

    @Query(value = """
        SELECT b.name, COUNT(*) AS cnt
        FROM listing l
        JOIN brand b ON b.id = l.brand_id
        WHERE l.created_at >= :from 
          AND l.created_at < :toPlus
          AND l.status = 'ACTIVE'
        GROUP BY b.name
        ORDER BY cnt DESC
        LIMIT :lim
    """, nativeQuery = true)
    List<Object[]> topBrands(Timestamp from, Timestamp toPlus, int lim);

    @Query(value = """
        SELECT m.name, COUNT(*) AS cnt
        FROM listing l
        JOIN model m ON m.id = l.model_id
        WHERE l.created_at >= :from 
          AND l.created_at < :toPlus
          AND l.status = 'ACTIVE'
        GROUP BY m.name
        ORDER BY cnt DESC
        LIMIT :lim
    """, nativeQuery = true)
    List<Object[]> topModels(Timestamp from, Timestamp toPlus, int lim);

    @Query(value = """
        SELECT c.name, COALESCE(AVG(l.price),0)
        FROM listing l
        JOIN category c ON c.id = l.category_id
        WHERE l.price IS NOT NULL
          AND l.created_at >= :from 
          AND l.created_at < :toPlus
          AND l.status = 'ACTIVE'
        GROUP BY c.name
    """, nativeQuery = true)
    List<Object[]> avgPriceByCategory(Timestamp from, Timestamp toPlus);

}
