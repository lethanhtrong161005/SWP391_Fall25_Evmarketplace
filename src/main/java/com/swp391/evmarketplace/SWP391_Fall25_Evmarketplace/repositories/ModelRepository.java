package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Model;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections.ModelFlat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelRepository extends JpaRepository<Model, Long> {

    List<Model> findByCategoryIdOrderByBrandNameAscNameAsc(Long categoryId);


    @Query("""
           select
             m.id   as id,
             m.name as name,
             m.year as year,
             m.brand.id as brandId,
             m.category.id as categoryId
           from Model m
           """)
    List<ModelFlat> findAllFlat();

    @Query("""
              select m.id as id, m.name as name, m.year as year,
                        m.brand.id as brandId, m.category.id as categoryId
              from Model m
              where m.category.id = :categoryId
              order by m.brand.name asc, m.name asc
            """)
    List<ModelFlat> findAllFlatByCategoryId(Long categoryId);
}
