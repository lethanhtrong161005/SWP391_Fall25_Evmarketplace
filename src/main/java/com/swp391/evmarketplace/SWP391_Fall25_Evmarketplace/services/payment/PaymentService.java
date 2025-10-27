package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.payment;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface PaymentService {

    // Tạo URL thanh toán promote cho listing
    BaseResponse<String> createPromotionPaymentUrl(Long listingId, HttpServletRequest request);

    // Đánh dấu thanh toán thành công theo vnp_TxnRef
    void markPaymentPaidByTxnRef(String txnRef, long amountVnd);

    // Xử lý return & ipn
    BaseResponse<Map<String,Object>> handleVnpReturn(org.springframework.util.MultiValueMap<String,String> queryParams);
    String handleVnpIpn(org.springframework.util.MultiValueMap<String,String> queryParams);

    BaseResponse<?> createOrderPaymentUrl(Long orderId, Long amountVnd, HttpServletRequest request);
    BaseResponse<?> recordCashPayment(Long orderId, Long amountVnd, String referenceNo, String note);

}
