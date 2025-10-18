package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.listing;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ListingStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.Visibility;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ListingHistoryDto {
    private Long historyId;

    private Long listingId;
    private String listingTitle;

    private ListingStatus fromStatus;
    private ListingStatus toStatus;
    private String reason;
    private String note;
    private LocalDateTime createdAt;

    private String categoryName;
    private BigDecimal price;
    private Visibility visibility;
    private String province;

    private Long actorId;
    private String actorFullName;
}
