package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.payment;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.config.VNPayProperties;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.SalePayment;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ListingStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.PaymentMethod;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.PaymentPurpose;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.PaymentStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ConfigRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ListingRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.SalePaymentRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.vnpay.VNPayService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.AuthUtil;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.UUIDUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class PaymentServiceImp implements PaymentService {
    @Autowired
    private SalePaymentRepository salePaymentRepository;
    @Autowired
    private  ListingRepository listingRepository;
    @Autowired
    private ConfigRepository configRepository;
    @Autowired
    private AuthUtil authUtil;
    @Autowired
    private VNPayService vnPayService;
    @Autowired
    private UUIDUtil uuidUtil;

    private long cfgLong(String key, long def) {
        return configRepository.findById(key)
                .map(c -> { try { return Long.parseLong(c.getValue()); } catch (Exception e){ return def; }})
                .orElse(def);
    }

    @Override
    public BaseResponse<String> createPromotionPaymentUrl(Long listingId, HttpServletRequest request) {
        var me = authUtil.getCurrentAccount();
        var listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));

        if (!listing.getSeller().getId().equals(me.getId()))
            throw new CustomBusinessException("Not your listing");

        if (listing.getStatus() != ListingStatus.APPROVED)
            throw new CustomBusinessException("Listing must be APPROVED to promote");

        long feeVnd = cfgLong("promoted_fee_vnd", 50000L);

        var pay = new SalePayment();
        pay.setListing(listing);
        pay.setPayer(me);
        pay.setAmount(BigDecimal.valueOf(feeVnd));
        pay.setMethod(PaymentMethod.VNPAY);
        pay.setPurpose(PaymentPurpose.PROMOTION);
        pay.setStatus(PaymentStatus.INIT);
        salePaymentRepository.save(pay);

        String txnRef = "PROMO-" + uuidUtil.generateDigits() + "-" + pay.getId();
        pay.setProviderTxnId(txnRef);
        salePaymentRepository.save(pay);

        Map<String,String> returnParams = Map.of(
                "purpose","PROMOTION",
                "listingId", String.valueOf(listingId),
                "paymentId", String.valueOf(pay.getId())
        );

        String paymentUrl = vnPayService.createPaymentUrl(
                feeVnd,
                "Thanh toan promote tin #" + listingId,
                VNPayProperties.getIpAddress(request),
                txnRef,
                returnParams,
                false
        );

        BaseResponse<String> res = new BaseResponse<>();
        res.setData(paymentUrl != null ? paymentUrl : "");
        res.setStatus(paymentUrl != null ? 200 : 500);
        res.setSuccess(paymentUrl != null);
        res.setMessage(paymentUrl != null ? "Get PaymentUrl Success" : "Get PaymentUrl Failed");
        return res;
    }

    @Override
    public void markPaymentPaidByTxnRef(String txnRef, long amountVnd) {
        var pay = salePaymentRepository.findByProviderTxnId(txnRef)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found by vnp_TxnRef: " + txnRef));
        if (pay.getStatus() == PaymentStatus.PAID) return; // idempotent

        if (pay.getAmount().longValue() != amountVnd) {
            pay.setStatus(PaymentStatus.FAILED);
        } else {
            pay.setStatus(PaymentStatus.PAID);
            pay.setPaidAt(LocalDateTime.now());
        }
        salePaymentRepository.save(pay);
    }

    @Override
    public BaseResponse<Map<String, Object>> handleVnpReturn(MultiValueMap<String, String> queryParams) {
        Map<String, String> p = vnPayService.flatten(queryParams);
        boolean valid = vnPayService.verifySignature(p);
        String resp = p.getOrDefault("vnp_ResponseCode", "");
        String trans = p.getOrDefault("vnp_TransactionStatus", "");
        String txnRef = p.get("vnp_TxnRef");
        long amountVnd = vnPayService.parseAmountVnd(p.get("vnp_Amount"));

        Map<String,Object> data = new HashMap<>();
        data.put("validSignature", valid);
        data.put("vnp_ResponseCode", resp);
        data.put("vnp_TransactionStatus", trans);
        data.put("vnp_TxnRef", txnRef);

        if (txnRef != null) {
            salePaymentRepository.findByProviderTxnId(txnRef).ifPresent(pay -> {
                data.put("purpose", pay.getPurpose().name());
                data.put("paymentId", pay.getId());
                if (pay.getListing() != null) data.put("listingId", pay.getListing().getId());
                data.put("currentStatus", pay.getStatus().name());
            });
        }

        var res = new BaseResponse<Map<String,Object>>();
        res.setData(data);
        res.setStatus(200);

        if (valid && "00".equals(resp) && "00".equals(trans) && txnRef != null) {
            try { markPaymentPaidByTxnRef(txnRef, amountVnd); }
            catch (Exception ex) {
                res.setSuccess(false);
                res.setMessage("PAYMENT_MARK_FAILED: " + ex.getMessage());
                return res;
            }
            res.setSuccess(true);
            res.setMessage("PAYMENT_SUCCESS");
            return res;
        }

        if (txnRef != null) {
            salePaymentRepository.findByProviderTxnId(txnRef).ifPresent(pay -> {
                if (pay.getStatus() != PaymentStatus.PAID) {
                    pay.setStatus(PaymentStatus.FAILED);
                    salePaymentRepository.save(pay);
                }
            });
        }
        res.setSuccess(false);
        res.setMessage(valid ? "PAYMENT_FAILED" : "INVALID_SIGNATURE");
        return res;
    }

    @Override
    public String handleVnpIpn(MultiValueMap<String, String> queryParams) {
        Map<String, String> p = vnPayService.flatten(queryParams);
        log.info("[VNPay IPN] {}", p);
        if (!vnPayService.verifySignature(p)) return "invalid-signature";

        String resp = p.getOrDefault("vnp_ResponseCode", "");
        String trans = p.getOrDefault("vnp_TransactionStatus", "");
        String txnRef = p.get("vnp_TxnRef");
        long amountVnd = vnPayService.parseAmountVnd(p.get("vnp_Amount"));

        if ("00".equals(resp) && "00".equals(trans) && txnRef != null) {
            try { markPaymentPaidByTxnRef(txnRef, amountVnd); return "ok"; }
            catch (Exception e) { log.error("IPN mark error", e); return "failed"; }
        }

        if (txnRef != null) {
            salePaymentRepository.findByProviderTxnId(txnRef).ifPresent(pay -> {
                if (pay.getStatus() != PaymentStatus.PAID) {
                    pay.setStatus(PaymentStatus.FAILED);
                    salePaymentRepository.save(pay);
                }
            });
        }
        return "failed";
    }

}
