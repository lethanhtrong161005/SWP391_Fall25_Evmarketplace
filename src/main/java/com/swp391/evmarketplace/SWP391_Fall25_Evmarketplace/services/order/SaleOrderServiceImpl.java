package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.order;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.order.CreateOrderBuyRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.order.OrderSearchRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.PageResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.order.SaleOrderDto;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Account;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Listing;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.SaleOrder;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.*;
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
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.domain.Specification;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public BaseResponse<?> getOrderDetails(Long orderId) {
        return null;
    }

    @Override
    @Transactional
    public BaseResponse<?> cancelOrder(Long orderId) {
        var order = saleOrderRepository.findById(orderId).orElseThrow(() -> new CustomBusinessException("Order Not Found"));

        if(order.getStatus() != OrderStatus.INITIATED && order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new CustomBusinessException("Can't cancel Order");
        }

        if(order.getPaidAmount() != null &&  order.getPaidAmount().compareTo(BigDecimal.ZERO) > 0){
            throw new CustomBusinessException("Order has payment in progress or captured");
        }

        Account log = authUtil.getCurrentAccount();
        if(log.getRole() == AccountRole.MEMBER){
            if(!log.getId().equals(order.getBuyer().getId())) {
                throw new CustomBusinessException("You are not the owner of this order");
            }
        }

        order.setStatus(OrderStatus.CANCELED);
        Listing l = order.getListing();
        if(l.getStatus() == ListingStatus.RESERVED){
            l.setStatus(ListingStatus.ACTIVE);
            listingRepository.save(l);
        }
        saleOrderRepository.save(order);
        saleOrderRepository.flush();

        BaseResponse<?> res = new  BaseResponse<>();
        res.setMessage("Order Cancelled");
        res.setSuccess(true);
        res.setStatus(200);
        return res;
    }

    @Override
    public BaseResponse<?> getAllOrdersByUserId(
            Long userId, String orderNo, OrderStatus status,
            int size, int page, String sort, String dir,
            LocalDateTime start, LocalDateTime end) {

        Account user = accountRepository.findById(userId)
                .orElseThrow(() -> new CustomBusinessException("User not found"));

        OrderSearchRequest c = new OrderSearchRequest(orderNo, status, size, page, sort, dir, start, end);

        return switch (user.getRole()) {
            case STAFF  -> getAllOrdersByStaffId(userId, c);
            case MEMBER -> getAllOrdersByMemberId(userId, c);
            default     -> throw new CustomBusinessException("User not authorized");
        };
    }

    private BaseResponse<?> getAllOrdersByStaffId(Long staffId, OrderSearchRequest c) {
        return searchOrders(SaleOrderSpecs.ofStaff(staffId), c);
    }

    private BaseResponse<?> getAllOrdersByMemberId(Long memberId, OrderSearchRequest c) {
        return searchOrders(SaleOrderSpecs.ofBuyer(memberId), c);
    }

    private Pageable buildPageable(OrderSearchRequest c) {
        Sort.Direction direction = "asc".equalsIgnoreCase(c.getDir()) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortField = switch (String.valueOf(c.getSort())) {
            case "amount", "paidAmount", "status", "updatedAt", "createdAt" -> c.getSort();
            default -> "createdAt";
        };
        int page = c.getPage() != null ? Math.max(c.getPage(), 0) : 0;
        int size = c.getSize() != null ? Math.max(c.getSize(), 1) : 10;
        return PageRequest.of(page, size, Sort.by(direction, sortField));
    }

    private <D> BaseResponse<PageResponse<D>> okPage(Page<D> page) {
        PageResponse<D> pr = new PageResponse<>();
        pr.setItems(page.getContent());
        pr.setTotalElements(page.getTotalElements());
        pr.setTotalPages(page.getTotalPages());
        pr.setSize(page.getSize());
        pr.setPage(page.getNumber());
        pr.setHasNext(page.hasNext());
        pr.setHasPrevious(page.hasPrevious());

        BaseResponse<PageResponse<D>> res = new BaseResponse<>();
        res.setData(pr);
        res.setMessage("Get Order Success");
        res.setStatus(200);
        res.setSuccess(true);
        return res;
    }

    private BaseResponse<?> searchOrders(Specification<SaleOrder> actorSpec, OrderSearchRequest c) {
        Specification<SaleOrder> spec = Specification.allOf(
                actorSpec,
                SaleOrderSpecs.hasStatus(c.getStatus()),
                SaleOrderSpecs.orderNoLike(c.getOrderNo()),
                SaleOrderSpecs.createdBetween(c.getStart(), c.getEnd())
        );

        Page<SaleOrder> entityPage = saleOrderRepository.findAll(spec, buildPageable(c));
        Page<SaleOrderDto> dtoPage = entityPage.map(e -> e.toDto(e));
        return okPage(dtoPage);
    }

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

        var buyer = accountRepository.findByPhoneNumber(req.getBuyerPhoneNumber()).orElseThrow(() -> new CustomBusinessException("Buyer phone number not found"));
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

        Set<OrderStatus> OPEN_STATUSES = EnumSet.of(
                OrderStatus.INITIATED,
                OrderStatus.PENDING_PAYMENT,
                OrderStatus.PAID,
                OrderStatus.CONTRACT_SIGNED
        );

        if (saleOrderRepository.existsByListing_IdAndStatusIn(listing.getId(), OPEN_STATUSES)) {
            throw new CustomBusinessException("Listing already has an open order");
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
