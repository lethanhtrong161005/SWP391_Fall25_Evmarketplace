package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.statistics.revenueSummary;


import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.statistics.RevenueSummaryDTO;

import java.time.LocalDate;

public interface RevenueSummaryService {
    RevenueSummaryDTO getRevenueSummary(LocalDate from, LocalDate to);
}
