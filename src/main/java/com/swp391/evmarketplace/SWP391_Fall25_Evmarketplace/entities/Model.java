package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.model.ModelDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.BrandStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ModelStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "model",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_model_name_brand_category_year",
                columnNames = {"name","brand_id","category_id","year"})
      )
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "smallint unsigned")
    @Min(1900) @Max(2100)
    private Integer year;

    @Column(name = "status" , nullable = false)
    @Enumerated(EnumType.STRING)
    private ModelStatus status = ModelStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "brand_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_model_brand")
    )
    private Brand brand;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false,
                foreignKey = @ForeignKey(name =  "fk_model_category")
    )
    private Category category;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public ModelDTO toDTO(Model model) {
        ModelDTO modelDTO = new ModelDTO();
        modelDTO.setId(model.getId());
        modelDTO.setName(model.getName());
        modelDTO.setYear(model.getYear());
        modelDTO.setStatus(model.getStatus() != null ? model.getStatus().name() : null);
        modelDTO.setBrandId(model.getBrand().getId());
        modelDTO.setCategoryId(model.getCategory().getId());
        modelDTO.setCategoryName(model.getCategory().getName());
        modelDTO.setBrandName(model.getBrand().getName());
        return modelDTO;
    }

}
