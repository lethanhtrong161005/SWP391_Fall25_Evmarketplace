package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.statistics.revenueSummary;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.statistics.RevenueSummaryDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ReportRevenueRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ReportTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class RevenueSummaryServiceImp implements RevenueSummaryService {
    @Autowired
    ReportRevenueRepository reportRevenueRepository;
    @Autowired
    ReportTransactionRepository reportTransactionRepository;

    @Override
    public RevenueSummaryDTO getRevenueSummary(LocalDate from, LocalDate to) {

        boolean noRange = (from == null && to == null);
        if (noRange) {
            from = LocalDate.of(1970, 1, 1);
            to = LocalDate.of(2100, 1, 1);
        } else if (from == null) {
            from = to.minusDays(30);
        } else if (to == null) {
            to = LocalDate.now();
        }

        // ---- Tổng doanh thu (PAID) ----
        BigDecimal totalRevenue = reportRevenueRepository.sumPaidAmount(from, to);

        // ---- Doanh thu theo source (map purpose -> business source) ----
        Map<String, BigDecimal> byPurpose = reportRevenueRepository.sumPaidAmountByPurpose(from, to);
        BigDecimal post = byPurpose.getOrDefault("LISTING_FEE", BigDecimal.ZERO);
        BigDecimal consignment =byPurpose.getOrDefault("ORDER", BigDecimal.ZERO);
        BigDecimal other = totalRevenue.subtract(post).subtract(consignment);

        Map<String, BigDecimal> revenueBySource = new LinkedHashMap<>();
        revenueBySource.put("POST", post);
        revenueBySource.put("CONSIGNMENT", consignment);
        revenueBySource.put("OTHER", other.max(BigDecimal.ZERO)); // tránh âm do sai lệch dữ liệu

        // ---- TB/ngày ----
        long days = java.time.temporal.ChronoUnit.DAYS.between(from, to) + 1; // [from, to] inclusive
        BigDecimal avgPerDay = days <= 0
                ? BigDecimal.ZERO
                : totalRevenue.divide(BigDecimal.valueOf(days), 2, RoundingMode.HALF_UP);

        // ---- TB/người dùng trả tiền ----
        long distinctPayers = reportRevenueRepository.countDistinctPayingUsers(from, to);
        BigDecimal avgPerPayingUser = distinctPayers == 0
                ? BigDecimal.ZERO
                : totalRevenue.divide(BigDecimal.valueOf(distinctPayers), 2, RoundingMode.HALF_UP);

        // ---- TB/giao dịch ----
        long transactionsSuccess = reportTransactionRepository.countPaidSuccess(from, to);
        BigDecimal avgTransaction = transactionsSuccess == 0
                ? BigDecimal.ZERO
                : totalRevenue.divide(BigDecimal.valueOf(transactionsSuccess), 2, RoundingMode.HALF_UP);


        // ---- Tỉ lệ doanh thu ký gửi / tổng doanh thu ----
        double consignmentShare = totalRevenue.compareTo(BigDecimal.ZERO) == 0
                ? 0.0
                : consignment.divide(totalRevenue, 6, java.math.RoundingMode.HALF_UP).doubleValue();

        return RevenueSummaryDTO.builder()
                .from(noRange ? null : from.toString())
                .to(noRange ? null : to.toString())
                .currency("VND")

                .totalRevenue(totalRevenue)
                .revenueBySource(revenueBySource)
                .averagePerDay(avgPerDay)
                .averagePerPayingUser(avgPerPayingUser)
                .averageTransactionValue(avgTransaction)
                .consignmentListingRevenueRate(consignmentShare)
                .build();
    }

//    private BigDecimal sum(Map<String, BigDecimal> m, String key) {
//        return m.getOrDefault(key, BigDecimal.ZERO);
//    }

}
