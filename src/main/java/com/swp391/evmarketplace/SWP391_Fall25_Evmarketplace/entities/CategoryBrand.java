package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "category_brand")
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
public class CategoryBrand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name="category_id",
            nullable=false,
            foreignKey=@ForeignKey(name="fk_category_brand_category"))
    private Category category;

    @ManyToOne(optional = false)
    @JoinColumn(name="brand_id",
            nullable=false,
            foreignKey=@ForeignKey(name="fk_category_brand_brand"))
    private Brand brand;
}
