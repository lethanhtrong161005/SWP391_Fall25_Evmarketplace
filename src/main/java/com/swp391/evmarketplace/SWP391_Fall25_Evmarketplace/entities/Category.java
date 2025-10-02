package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.category.CategoryResponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.CategoryStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name ="category")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Category {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true, length=255)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "status" , nullable = false)
    @Enumerated(EnumType.STRING)
    private CategoryStatus status = CategoryStatus.ACTIVE;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<CategoryBrand> categoryBrands = new ArrayList<>();

    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private List<Listing> listings = new ArrayList<>();


    public void addCategoryBrand(CategoryBrand cb){
        categoryBrands.add(cb);
        cb.setCategory(this);
    }
    public void removeCategoryBrand(CategoryBrand cb){
        categoryBrands.remove(cb);
        cb.setCategory(null);
    }

    public CategoryResponseDTO toDto(Category category){
        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setStatus(category.getStatus().name());
        return dto;
    }

}
