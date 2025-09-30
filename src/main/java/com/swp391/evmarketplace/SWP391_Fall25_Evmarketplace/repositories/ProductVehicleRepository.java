package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ProductVehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductVehicleRepository extends JpaRepository<ProductVehicle, Long> {
    Optional<ProductVehicle> findFirstByCategoryIdAndBrandIdAndModelId(Long categoryId, Long brandId, Long modelId);
}
