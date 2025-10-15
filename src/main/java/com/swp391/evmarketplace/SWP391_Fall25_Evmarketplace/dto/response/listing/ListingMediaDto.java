package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.listing;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.MediaType;
import lombok.Data;

@Data
public class ListingMediaDto {
    private Long id;
    private String mediaUrl;
    private MediaType mediaType;

}
