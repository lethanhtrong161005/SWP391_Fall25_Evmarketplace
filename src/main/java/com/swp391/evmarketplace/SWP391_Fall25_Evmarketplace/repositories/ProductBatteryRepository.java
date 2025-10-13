package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ProductBattery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface ProductBatteryRepository extends JpaRepository<ProductBattery, Long> {

    Optional<ProductBattery> findFirstByCategoryIdAndBrandIdAndModelId(Long categoryId, Long brandId, Long modelId);

    boolean existsByBrand_IdAndModel_IdAndCapacityKwhAndVoltage(
            Long brandId, Long modelId, BigDecimal capacityKwh, BigDecimal voltage
    );

    @EntityGraph(attributePaths = {"brand","model","category"})
    @Query("select pb from ProductBattery pb")
    Page<ProductBattery> findAllWithGraph(Pageable pageable);

    boolean existsByBrand_IdAndModel_IdAndCapacityKwhAndVoltageAndIdNot(
            Long brandId, Long modelId, BigDecimal capacityKwh, BigDecimal voltage, Long idNot
    );


    @EntityGraph(attributePaths = {"brand","model","category"})
    Optional<ProductBattery> findFirstByCategory_IdAndBrand_IdAndModel_Id(
            Long categoryId, Long brandId, Long modelId
    );



}
