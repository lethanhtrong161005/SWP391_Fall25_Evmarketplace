package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.favorite;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.Visibility;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteDto {
    private Long id;
    private Long listingId;
    private String title;
    private BigDecimal price;

    private String province;
    private String district;
    private String ward;
    private String address;
    
    private LocalDateTime timeAgo;        // "1 tuần trước" (optional)
    private Visibility visibility;
    private String thumbnailUrl;   // ảnh đại diện
    private LocalDateTime favoredAt;
    private Boolean consigned;

}
