package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom;

import java.util.List;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PageResponse<T>{
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;
    private int page;
    private int size;
    private List<T> items;
}
