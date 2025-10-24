package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.payment;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.config.VNPayProperties;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.SalePayment;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.*;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ConfigRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ListingRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.SaleOrderRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.SalePaymentRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.config.ConfigService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.vnpay.VNPayService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.AuthUtil;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.UUIDUtil;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
    @Autowired
    private SaleOrderRepository saleOrderRepository;
    @Autowired
    private EntityManager entityManager;


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
        int payMins = (int) cfgLong("vnpay_payment_minutes_promo", 20L);

        var pay = new SalePayment();
        pay.setListing(listing);
        pay.setPayer(me);
        pay.setAmount(BigDecimal.valueOf(feeVnd));
        pay.setMethod(PaymentMethod.VNPAY);
        pay.setPurpose(PaymentPurpose.PROMOTION);
        pay.setStatus(PaymentStatus.INIT);

        var zone = ZoneId.of("Asia/Ho_Chi_Minh");
        var nowZ = ZonedDateTime.now(zone);
        var expireAt = nowZ.plusMinutes(payMins);


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
                "Payment Listing #" + listingId,
                VNPayProperties.getIpAddress(request),
                txnRef,
                returnParams,
                false,
                expireAt
        );

        BaseResponse<String> res = new BaseResponse<>();
        res.setData(paymentUrl != null ? paymentUrl : "");
        res.setStatus(paymentUrl != null ? 200 : 500);
        res.setSuccess(paymentUrl != null);
        res.setMessage(paymentUrl != null ? "Get PaymentUrl Success" : "Get PaymentUrl Failed");
        return res;
    }

    @Override
    @Transactional
    public BaseResponse<?> createOrderPaymentUrl(Long orderId, Long amountVnd, HttpServletRequest request) {
        var me = authUtil.getCurrentAccount();
        var order = saleOrderRepository.findById(orderId)
                .orElseThrow(() -> new CustomBusinessException("Order not found"));

        if (!order.getBuyer().getId().equals(me.getId()))
            throw new CustomBusinessException("Not your order");

        switch (order.getStatus()) {
            case INITIATED, PENDING_PAYMENT -> {}
            default -> throw new CustomBusinessException("Order is not payable in status= " + order.getStatus());
        }

        if (order.getReservedUntil() != null && LocalDateTime.now().isAfter(order.getReservedUntil()))
            throw new CustomBusinessException("Reservation expired. Please create a new order");

        BigDecimal total   = order.getAmount();
        BigDecimal paid    = order.getPaidAmount() == null ? BigDecimal.ZERO : order.getPaidAmount();
        BigDecimal remain  = total.subtract(paid);
        if (remain.compareTo(BigDecimal.ZERO) <= 0)
            throw new CustomBusinessException("Order already fully paid.");

        long cap = cfgLong("vnpay_txn_max_vnd", 100_000_000L);
        long remainVnd = remain.setScale(0, BigDecimal.ROUND_HALF_UP).longValue();

        boolean userSpecified = amountVnd != null && amountVnd > 0;
        long wantVnd = userSpecified ? amountVnd : remainVnd;

        if(wantVnd > remainVnd){
            wantVnd = remainVnd;
        }

        if(wantVnd > cap){
            if(userSpecified){
                throw new CustomBusinessException( "Amount exceeds per-transaction limit (" + cap + " VND). Please split into multiple payments.");
            }else{
                wantVnd = cap;
            }
        }

        if(wantVnd < 0){
            throw new CustomBusinessException("Amount must be positive.");
        }

        int payMins = (int) cfgLong("vnpay_payment_minutes", 60L);
        var zone = ZoneId.of("Asia/Ho_Chi_Minh");
        var nowZ = ZonedDateTime.now(zone);
        var clamp = nowZ.plusMinutes(payMins);

        if (order.getReservedUntil() != null) {
            var safe = order.getReservedUntil().atZone(zone).minusSeconds(60);
            if (clamp.isAfter(safe)) clamp = safe;
            if (clamp.isBefore(nowZ.plusMinutes(1)))
                throw new CustomBusinessException("Reservation almost expired. Please create a new order");
        }


        var pay = new SalePayment();
        pay.setOrder(order);
        pay.setListing(order.getListing());
        pay.setPayer(me);
        pay.setAmount(BigDecimal.valueOf(wantVnd));
        pay.setMethod(PaymentMethod.VNPAY);
        pay.setPurpose(PaymentPurpose.ORDER);
        pay.setStatus(PaymentStatus.INIT);
        pay.setExpiresAt(clamp.toLocalDateTime());
        salePaymentRepository.save(pay);

        String txnRef = "ORDER-" + uuidUtil.generateDigits() + "-" + pay.getId();
        pay.setProviderTxnId(txnRef);
        salePaymentRepository.save(pay);

        var returnParams = java.util.Map.of(
                "purpose", "ORDER",
                "orderId", String.valueOf(order.getId()),
                "paymentId", String.valueOf(pay.getId())
        );

        String paymentUrl = vnPayService.createPaymentUrl(
                wantVnd,
                "Payment Order #" + order.getId(),
                VNPayProperties.getIpAddress(request),
                txnRef,
                returnParams,
                false,
                clamp
        );

        var res = new BaseResponse<String>();
        res.setData(paymentUrl != null ? paymentUrl : "");
        res.setStatus(paymentUrl != null ? 200 : 500);
        res.setSuccess(paymentUrl != null);
        res.setMessage(paymentUrl != null ? "Get PaymentUrl Success" : "Get PaymentUrl Failed");
        return res;
    }


    @Override
    @Transactional
    public BaseResponse<?> recordCashPayment(Long orderId, Long amountVnd, String referenceNo, String note) {
        var me = authUtil.getCurrentAccount();

        var order = saleOrderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new CustomBusinessException("Order Not Found."));

        if(me.getRole() == AccountRole.MEMBER || me.getRole() == AccountRole.MODERATOR){
            throw new CustomBusinessException("You are not allowed to perform this action.");
        }

        if(order.getStatus() != OrderStatus.INITIATED && order.getStatus() != OrderStatus.PENDING_PAYMENT){
            throw new CustomBusinessException("Order is not payable in status= " + order.getStatus());
        }

        BigDecimal total = order.getAmount();
        BigDecimal paid = order.getPaidAmount() == null ? BigDecimal.ZERO : order.getPaidAmount();
        BigDecimal remaining = total.subtract(paid);

        if(remaining.signum() < 0){
            throw new CustomBusinessException("Order already fully paid");
        }

        long wantVnd = (amountVnd == null || amountVnd < 0)
                ? remaining.setScale(0, RoundingMode.HALF_UP).longValue()
                : amountVnd;

        if(wantVnd <= 0){
            throw new CustomBusinessException("Amount must be positive.");
        }

        if(wantVnd > remaining.longValue()){
            wantVnd = remaining.longValue();
        }

        BigDecimal payAmt = BigDecimal.valueOf(wantVnd);


        var pay = new SalePayment();
        pay.setOrder(order);
        pay.setListing(order.getListing());
        pay.setPayer(order.getBuyer());
        pay.setAmount(payAmt);
        pay.setMethod(PaymentMethod.CASH);
        pay.setPurpose(PaymentPurpose.ORDER);
        pay.setStatus(PaymentStatus.INIT);
        pay.setReferenceNo(referenceNo);
        pay.setRecordedBy(me);
        pay.setNote(note);
        salePaymentRepository.save(pay);


        pay.setPaidAt(LocalDateTime.now());
        pay.setStatus(PaymentStatus.PAID);
        salePaymentRepository.save(pay);

        entityManager.refresh(order);

        var res = new BaseResponse<Map<String, Object>>();
        res.setData(Map.of(
                "paymentId", pay.getId(),
                "paid", payAmt,
                "method", pay.getMethod(),
                "orderPaidAmount", order.getPaidAmount(),
                "orderStatus", order.getStatus()
        ));
        res.setStatus(200);
        res.setMessage("Payment Success");
        res.setSuccess(true);
        return res;

    }

    @Override
    public void markPaymentPaidByTxnRef(String txnRef, long amountVnd) {
        var pay = salePaymentRepository.findByProviderTxnId(txnRef)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found by vnp_TxnRef: " + txnRef));
        if (pay.getStatus() == PaymentStatus.PAID) return;

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
