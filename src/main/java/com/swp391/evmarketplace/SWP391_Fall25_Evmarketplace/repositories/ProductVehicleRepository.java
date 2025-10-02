package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ProductVehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVehicleRepository extends JpaRepository<ProductVehicle, Long> {
    Optional<ProductVehicle> findFirstByCategoryIdAndBrandIdAndModelId(Long categoryId, Long brandId, Long modelId);

    boolean existsByBrand_IdAndModel_IdAndNameIgnoreCaseAndReleaseYear(
            Long brandId, Long modelId, String name, Integer releaseYear
    );


    // Dùng khi update để loại trừ chính nó
    boolean existsByBrand_IdAndModel_IdAndNameIgnoreCaseAndReleaseYearAndIdNot(
            Long brandId, Long modelId, String name, Integer releaseYear, Long idNot
    );

    @EntityGraph(attributePaths = {
            "category", "brand", "model",
            "carDetail", "bikeDetail", "ebikeDetail"
    })
    @Query("select pv from ProductVehicle pv")
    List<ProductVehicle> findAllWithGraph();

    @EntityGraph(attributePaths = {
            "category", "brand", "model",
            "carDetail", "bikeDetail", "ebikeDetail"
    })
    ProductVehicle findWithGraphById(Long id);

    @EntityGraph(attributePaths = {"category","brand","model","carDetail","bikeDetail","ebikeDetail"})
    @Query("select pv from ProductVehicle pv")
    Page<ProductVehicle> findAllWithGraph(Pageable pageable);

    // Lấy top N + sort (dùng native hơi tiện)
    @EntityGraph(attributePaths = {"category","brand","model","carDetail","bikeDetail","ebikeDetail"})
    @Query(value = """
        select * from product_vehicle
        order by 
          CASE WHEN :#{#sort.isAscending()} THEN 0 END, 
          id desc
        limit :limit
        """, nativeQuery = true)
    List<ProductVehicle> findTopNWithGraph(int limit, Sort sort);

}
