package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.brand.BrandResponseDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "brand",
        uniqueConstraints = @UniqueConstraint(name="uk_brand_name", columnNames = "name"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true, length=255)
    private String name;

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<CategoryBrand> categoryBrands = new ArrayList<>();

    public void addCategoryBrand(CategoryBrand cb){
        categoryBrands.add(cb);
        cb.setBrand(this);
    }
    public void removeCategoryBrand(CategoryBrand cb){
        categoryBrands.remove(cb);
        cb.setBrand(null);
    }

    public BrandResponseDTO toDTO(Brand b){
        BrandResponseDTO dto = new BrandResponseDTO();
        dto.setId(b.getId());
        dto.setName(b.getName());
        return dto;
    }

}
