package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand, String> {

    /**
     * Lấy danh sách brand theo từng category id
     * @param categoryId
     * @return
     */
    @Query("""
        SELECT b FROM CategoryBrand cb
        JOIN cb.brand b
        WHERE cb.category.id = :categoryId
        ORDER BY b.name
    """)
    List<Brand> findByCategoryId(Long categoryId);

}
