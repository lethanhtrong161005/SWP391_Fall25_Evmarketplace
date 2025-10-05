package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.CategoryBrand;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections.CategoryBrandFlat;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections.CategoryBrandModelFlat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryBrandRepository extends JpaRepository<CategoryBrand,Long> {
    List<CategoryBrand> findByCategoryId(Long categoryId);
    List<CategoryBrand> findByBrandId(Long brandId);

    @Query("""
        select cb from CategoryBrand cb
        join fetch cb.category c
        join fetch cb.brand b
        where c.id = :categoryId
        order by b.name asc
    """)
    List<CategoryBrand> findByCategoryIdFetch(@Param("categoryId") Long categoryId);

    @Query(value = """
        SELECT
        c.id   AS categoryId,
        c.name AS categoryName,
        c.description AS categoryDescription,
        c.status AS categoryStatus,
        b.id   AS brandId,
        b.name AS brandName,
        b.status AS brandStatus,
        m.id   AS modelId,
        m.name AS modelName,
        m.year AS modelYear,
        m.status AS modelStatus
      FROM category_brand cb
        JOIN category c ON c.id = cb.category_id
        JOIN brand    b ON b.id = cb.brand_id
        LEFT JOIN model m
          ON m.category_id = cb.category_id
         AND m.brand_id    = cb.brand_id
      ORDER BY c.id ASC, b.name ASC, m.name ASC
  """, nativeQuery = true)
    List<CategoryBrandModelFlat> findAllCategoryBrandModelFlat();

    @Query(value = """
      SELECT
        c.id   AS categoryId,
        c.name AS categoryName,
        c.description AS categoryDescription,
        c.status AS categoryStatus,
        b.id   AS brandId,
        b.name AS brandName,
        b.status AS brandStatus,
        m.id   AS modelId,
        m.name AS modelName,
        m.year AS modelYear,
        m.status AS modelStatus
      FROM category_brand cb
        JOIN category c ON c.id = cb.category_id AND c.status = 'ACTIVE'
        JOIN brand    b ON b.id = cb.brand_id     AND b.status = 'ACTIVE'
        LEFT JOIN model m
          ON m.category_id = cb.category_id
         AND m.brand_id    = cb.brand_id
         AND m.status = 'ACTIVE'
      ORDER BY c.id ASC, b.name ASC, m.name ASC
  """, nativeQuery = true)
    List<CategoryBrandModelFlat> findAllCategoryBrandModelFlatActiveOnly();


    // CategoryBrandRepository
    @Query("""
     select cb.category.id   as categoryId,
         cb.category.name as categoryName,
         cb.category.description as categoryDescription,
         cb.brand.id      as brandId,
         cb.brand.name    as brandName
        from CategoryBrand cb
        where cb.category.id = :categoryId
        order by cb.brand.name asc
    """)
    List<CategoryBrandFlat> findFlatByCategoryId(Long categoryId);

    boolean existsByCategory_IdAndBrand_Id(Long categoryId, Long brandId);
    List<CategoryBrand> findByBrand_Id(Long brandId);

    // Cẩn thận: khi danh sách rỗng hãy xoá hết bằng deleteByBrand_Id
    void deleteByBrand_Id(Long brandId);
    void deleteByBrand_IdAndCategory_IdNotIn(Long brandId, List<Long> keepCategoryIds);
}
