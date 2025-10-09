package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;


public final class PageableUtils {
    private PageableUtils() {
    }

    public static final int MAX_SIZE = 100;

    public static Pageable buildPageable(int page, int size, String sort, String dir) {
        int p = Math.max(page, 0);
        int s = Math.min(Math.max(size, 1), MAX_SIZE);

        Sort base = (sort == null || sort.isBlank())
                ? Sort.by(Sort.Order.desc("createdAt"))
                : Sort.by("desc".equalsIgnoreCase(dir) ? Sort.Direction.DESC : Sort.Direction.ASC, sort);

        // tie-breaker
        Sort stable = base.and(Sort.by(Sort.Direction.DESC, "id"));

        return PageRequest.of(p, s, stable);
    }
}
