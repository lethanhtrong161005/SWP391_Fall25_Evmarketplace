package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.order;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.order.CreateOrderBuyRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Listing;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.SaleOrder;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.AccountRole;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.AccountStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ConsignmentAgreementStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ListingStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.AccountRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ListingRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.SaleOrderRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.SalePaymentRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.payment.PaymentService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.AuthUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SaleOrderServiceImpl implements SaleOrderSerivce {

    private final SaleOrderRepository saleOrderRepository;
    private final ListingRepository listingRepository;
    private final SalePaymentRepository salePaymentRepository;
    private final AccountRepository accountRepository;
    private final AuthUtil authUtil;
    private final PaymentService paymentService;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public BaseResponse<?> createOrder(CreateOrderBuyRequest req) {
        Listing listing = listingRepository.findById(req.getListingId())
                .orElseThrow(() -> new CustomBusinessException("Listing not found"));
        if (!Boolean.TRUE.equals(listing.getVerified())
                || !Boolean.TRUE.equals(listing.getConsigned())
                || listing.getStatus() != ListingStatus.ACTIVE) {
            throw new CustomBusinessException("Listing is not condition buy now");
        }

        if(listing.getConsignmentAgreement().getStatus() == ConsignmentAgreementStatus.CANCELLED){
            throw new CustomBusinessException("Consignment Agreement is cancelled");
        }

        if(listing.getConsignmentAgreement().getStatus() == ConsignmentAgreementStatus.EXPIRED || !listing.getConsignmentAgreement().getExpireAt().isAfter(LocalDateTime.now())){
            throw new CustomBusinessException("Consignment Agreement is expired");
        }

        if(authUtil.getCurrentAccount().getRole() == AccountRole.MEMBER || authUtil.getCurrentAccount().getRole() == AccountRole.MODERATOR ){
            throw new CustomBusinessException("You are not allowed to create an order");
        }

        var buyer = accountRepository.findById(req.getBuyerId()).orElseThrow(() -> new CustomBusinessException("Buyer not found"));
        if(buyer.getId().equals(listing.getSeller().getId())){
            throw new CustomBusinessException("Buyer cannot be the seller");
        }
        if(buyer.getId().equals(listing.getConsignmentAgreement().getOwner().getId())){
            throw new CustomBusinessException("Buyer cannot be the owner of consignment agreement");
        }

        if(buyer.getStatus() != AccountStatus.ACTIVE){
            throw new CustomBusinessException("Buyer is not active");
        }


        if(authUtil.getCurrentAccount().getRole() == AccountRole.STAFF){
            if(!authUtil.getCurrentAccount().getId().equals(listing.getConsignmentAgreement().getStaff().getId())){
                throw new CustomBusinessException("Staff is not allowed to create an order");
            }
        }

        if(saleOrderRepository.existsByListing_IdAndIsOpenTrue(listing.getId())){
            throw new CustomBusinessException("Listing already has an open order ");
        }

        try{
            SaleOrder order = new SaleOrder();
            order.setListing(listing);
            order.setBuyer(buyer);
            order.setSeller(listing.getSeller());
            order.setAmount(listing.getPrice());
            order.setCreatedBy(authUtil.getCurrentAccount());
            order.setBranch(listing.getBranch());
            order.setConsignmentAgreement(listing.getConsignmentAgreement());

            SaleOrder saved = saleOrderRepository.saveAndFlush(order);
            entityManager.refresh(saved);

            BaseResponse<Map<String, Object>> response = new BaseResponse<>();
            response.setMessage("Order created");
            response.setStatus(200);
            response.setSuccess(true);
            response.setData(Map.of(
                    "orderId", saved.getId(),
                    "status", saved.getStatus(),
                    "reservedUntil", saved.getReservedUntil(),
                    "listingId", listing.getId(),
                    "amount", saved.getAmount()
            ));
            return response;
        }catch (DataIntegrityViolationException ex){
            throw new  CustomBusinessException("Listing already has an open order");
        } catch(Exception e){
            throw new CustomBusinessException(e.getMessage());
        }

    }

}
