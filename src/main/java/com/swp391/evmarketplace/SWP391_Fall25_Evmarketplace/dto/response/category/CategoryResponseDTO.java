package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.category;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDTO {
    private Long id;
    private String name;
    private String description;
}
