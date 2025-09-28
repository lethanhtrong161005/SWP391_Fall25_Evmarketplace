package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name ="category",
        uniqueConstraints = @UniqueConstraint(name="uk_category_name", columnNames = "name"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Category {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true, length=255)
    private String name;

    @Column(length=500)
    private String description;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<CategoryBrand> categoryBrands = new ArrayList<>();


    public void addCategoryBrand(CategoryBrand cb){
        categoryBrands.add(cb);
        cb.setCategory(this);
    }
    public void removeCategoryBrand(CategoryBrand cb){
        categoryBrands.remove(cb);
        cb.setCategory(null);
    }

}
