package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.CategoryBrand;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections.CategoryBrandFlat;
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

    @Query("""
           select
             cb.category.id   as categoryId,
             cb.category.name as categoryName,
             cb.category.description as categoryDescription,
             cb.brand.id      as brandId,
             cb.brand.name    as brandName
           from CategoryBrand cb
           order by cb.category.id, cb.brand.id
           """)
    List<CategoryBrandFlat> findAllFlat();


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
