package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.contract;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.contract.ActivateContractRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.contract.CreateContractRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.contract.ContractDto;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.StoredContractResult;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.*;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ContractSignMethod;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ContractStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.OrderStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ContractRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.SaleOrderRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.file.FileService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final FileService fileService;
    private final SaleOrderRepository saleOrderRepository;
    private final ObjectMapper objectMapper;

    private static final Set<String> ALLOWED_SORT = Set.of(
            "createdAt", "signedAt", "effectiveFrom", "effectiveTo", "status", "signMethod",
            "orderNo", "orderCode", "listingTitle", "buyerName", "sellerName", "branchName"
    );

    @Transactional(readOnly = true)
    @Override
    public BaseResponse<?> getAllContracts(
            Long orderId,
            ContractStatus status,
            ContractSignMethod method,
            Long branchId,
            Long buyerId,
            Long sellerId,
            LocalDateTime createdFrom,
            LocalDateTime createdTo,
            LocalDateTime signedFrom,
            LocalDateTime signedTo,
            LocalDateTime effectiveFrom,
            LocalDateTime effectiveTo,
            String q,
            String sort,
            String dir,
            int page,
            int size
    ) {

        q = (q == null) ? null : q.trim();
        page = Math.max(0, page);
        size = Math.max(1, Math.min(size, 100));

        if (createdFrom != null && createdTo != null && createdFrom.isAfter(createdTo)) {
            LocalDateTime tmp = createdFrom; createdFrom = createdTo; createdTo = tmp;
        }
        if (signedFrom != null && signedTo != null && signedFrom.isAfter(signedTo)) {
            LocalDateTime tmp = signedFrom; signedFrom = signedTo; signedTo = tmp;
        }
        if (effectiveFrom != null && effectiveTo != null && effectiveFrom.isAfter(effectiveTo)) {
            LocalDateTime tmp = effectiveFrom; effectiveFrom = effectiveTo; effectiveTo = tmp;
        }

        String sortField = (sort == null || !ALLOWED_SORT.contains(sort)) ? "createdAt" : sort;
        Sort.Direction direction = "asc".equalsIgnoreCase(dir) ? Sort.Direction.ASC : Sort.Direction.DESC;

        Sort mappedSort = mapSort(sortField, direction);
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 100), mappedSort);

        Specification<Contract> spec = buildSpec(
                orderId, status, method, branchId, buyerId, sellerId,
                createdFrom, createdTo, signedFrom, signedTo, effectiveFrom, effectiveTo, q
        );

        Page<Contract> pageData = contractRepository.findAll(spec, pageable);

        List<ContractDto> rows = pageData.getContent().stream()
                .map(item -> item.toDto(item)).toList();

        Map<String, Object> payload = new HashMap<>();
        payload.put("items", rows);
        payload.put("page", pageData.getNumber());
        payload.put("size", pageData.getSize());
        payload.put("totalElements", pageData.getTotalElements());
        payload.put("totalPages", pageData.getTotalPages());
        payload.put("hasNext", pageData.hasNext());
        payload.put("hasPrevious", pageData.hasPrevious());

        BaseResponse<Object> res = new BaseResponse<>();
        res.setStatus(200);
        res.setSuccess(true);
        res.setMessage("Contracts fetched");
        res.setData(payload);
        return res;
    }

    /** Sort map “ảo” → field thực (dùng dot-path của Spring Data) */
    private Sort mapSort(String sortField, Sort.Direction dir) {
        if (Set.of("createdAt", "signedAt", "effectiveFrom", "effectiveTo", "status", "signMethod").contains(sortField)) {
            return Sort.by(dir, sortField);
        }
        return switch (sortField) {
            case "orderNo" -> Sort.by(dir, "order.orderNo");
            case "orderCode" -> Sort.by(dir, "order.orderCode");
            case "listingTitle" -> Sort.by(dir, "order.listing.title");
            case "buyerName" -> Sort.by(dir, "order.buyer.profile.fullName");
            case "sellerName" -> Sort.by(dir, "order.seller.profile.fullName");
            case "branchName" -> Sort.by(dir, "order.branch.name");
            default -> Sort.by(dir, "createdAt");
        };
    }

    private Specification<Contract> buildSpec(
            Long orderId,
            ContractStatus status,
            ContractSignMethod method,
            Long branchId,
            Long buyerId,
            Long sellerId,
            LocalDateTime createdFrom,
            LocalDateTime createdTo,
            LocalDateTime signedFrom,
            LocalDateTime signedTo,
            LocalDateTime effectiveFrom,
            LocalDateTime effectiveTo,
            String q
    ) {
        return (Root<Contract> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {

            Join<Contract, SaleOrder> jOrder = root.join("order", JoinType.LEFT);
            Join<SaleOrder, Listing> jListing = jOrder.join("listing", JoinType.LEFT);
            Join<SaleOrder, Account> jBuyer = jOrder.join("buyer", JoinType.LEFT);
            Join<SaleOrder, Profile> jBuyerProfile = jBuyer.join("profile", JoinType.LEFT);
            Join<SaleOrder, Account> jSeller = jOrder.join("seller", JoinType.LEFT);
            Join<SaleOrder, Profile> jSellerProfile = jSeller.join("profile", JoinType.LEFT);
            Join<SaleOrder, Branch> jBranch = jOrder.join("branch", JoinType.LEFT);

            List<Predicate> predicates = new ArrayList<>();

            if (orderId != null) predicates.add(cb.equal(jOrder.get("id"), orderId));
            if (status != null) predicates.add(cb.equal(root.get("status"), status));
            if (method != null) predicates.add(cb.equal(root.get("signMethod"), method));
            if (branchId != null) predicates.add(cb.equal(jBranch.get("id"), branchId));
            if (buyerId != null) predicates.add(cb.equal(jBuyer.get("id"), buyerId));
            if (sellerId != null) predicates.add(cb.equal(jSeller.get("id"), sellerId));

            if (createdFrom != null) predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), createdFrom));
            if (createdTo != null)   predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), createdTo));
            if (signedFrom != null)  predicates.add(cb.greaterThanOrEqualTo(root.get("signedAt"), signedFrom));
            if (signedTo != null)    predicates.add(cb.lessThanOrEqualTo(root.get("signedAt"), signedTo));
            if (effectiveFrom != null) predicates.add(cb.greaterThanOrEqualTo(root.get("effectiveFrom"), effectiveFrom));
            if (effectiveTo != null)   predicates.add(cb.lessThanOrEqualTo(root.get("effectiveTo"), effectiveTo));

            if (q != null && !q.isBlank()) {
                String like = "%" + q.trim().toLowerCase() + "%";

                Expression<String> orderCodeStr = cb.toString(jOrder.get("orderCode"));

                Predicate pQ = cb.or(
                        cb.like(cb.lower(jOrder.get("orderNo")), like),
                        cb.like(cb.lower(orderCodeStr), like),
                        cb.like(cb.lower(jListing.get("title")), like),
                        cb.like(cb.lower(jBuyerProfile.get("fullName")), like),
                        cb.like(cb.lower(jBuyer.get("phoneNumber")), like),
                        cb.like(cb.lower(jSellerProfile.get("fullName")), like),
                        cb.like(cb.lower(jSeller.get("phoneNumber")), like),
                        cb.like(cb.lower(jBranch.get("name")), like)
                );
                predicates.add(pQ);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }


    @Override
    @Transactional
    public BaseResponse<?> createContract(CreateContractRequest reqDto,
                                          MultipartFile contractFile,
                                          HttpServletRequest http) {

        SaleOrder order = saleOrderRepository.findById(reqDto.getOrderId())
                .orElseThrow(() -> new CustomBusinessException("Order not found"));

        if (order.getStatus() != OrderStatus.PAID && order.getStatus() != OrderStatus.CONTRACT_SIGNED) {
            throw new CustomBusinessException("Order must be PAID before attaching contract");
        }
        if (contractRepository.existsByOrder_Id(reqDto.getOrderId())) {
            throw new CustomBusinessException("Contract already exists for this order");
        }

        if (reqDto.getEffectiveFrom() == null) throw new CustomBusinessException("effectiveFrom is required");
        if (reqDto.getEffectiveTo() != null && reqDto.getEffectiveTo().isBefore(reqDto.getEffectiveFrom())) {
            throw new CustomBusinessException("effectiveTo must be after effectiveFrom");
        }

        final StoredContractResult upload;
        try {
            upload = fileService.storedContract(contractFile);
        } catch (Exception e) {
            throw new CustomBusinessException("Error while uploading file: " + e.getMessage());
        }

        ObjectNode paperEvt = buildPaperUploadedEvent(upload, reqDto.getStaffId(), http, reqDto.getNote());

        Contract contract = new Contract();
        contract.setOrder(order);
        contract.setFileUrl(upload.getFileName());
        contract.setSignMethod(ContractSignMethod.PAPER);
        contract.setSignedAt(null);
        contract.setEffectiveFrom(reqDto.getEffectiveFrom());
        contract.setEffectiveTo(reqDto.getEffectiveTo());
        contract.setSignLog(appendSignEvent(null, paperEvt));
        contract.setStatus(ContractStatus.UPLOADED);

        contractRepository.saveAndFlush(contract);

        BaseResponse<Object> res = new BaseResponse<>();
        res.setStatus(200);
        res.setData(Map.of("contractId", contract.getId()));
        res.setSuccess(true);
        res.setMessage("Paper contract created");
        return res;
    }

    @Override
    @Transactional
    public BaseResponse<?> activateContract(ActivateContractRequest reqDto, HttpServletRequest http) {
        Contract contract = contractRepository.findById(reqDto.getContractId())
                .orElseThrow(() -> new CustomBusinessException("Contract not found"));

        Long buyerId = contract.getOrder().getBuyer().getId();
        if (!Objects.equals(reqDto.getBuyerId(), buyerId)) {
            throw new CustomBusinessException("Forbidden: only buyer can activate the contract");
        }

        if (contract.getStatus() == ContractStatus.CANCELLED || contract.getStatus() == ContractStatus.EXPIRED) {
            throw new CustomBusinessException("Contract is not activatable");
        }

        if (contract.getStatus() == ContractStatus.ACTIVE) {
            return new BaseResponse<>(200, true, "Contract already active",
                    Map.of("contractId", contract.getId(), "status", contract.getStatus().name()), null, LocalDateTime.now());
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime efFrom = contract.getEffectiveFrom();
        LocalDateTime efTo = contract.getEffectiveTo();

        if (efTo != null && now.isAfter(efTo)) {
            throw new CustomBusinessException("Contract already expired");
        }

        if (efFrom == null) {
            contract.setEffectiveFrom(now);
            efFrom = now;
        }

        ObjectNode clickEvent = buildClientClickEvent(buyerId, http);
        contract.setSignLog(appendSignEvent(contract.getSignLog(), clickEvent));
        contract.setSignMethod(ContractSignMethod.CLICK);
        contract.setSignedAt(now);

        contract.setStatus(!efFrom.isAfter(now) ? ContractStatus.ACTIVE : ContractStatus.SIGNED);
        contractRepository.saveAndFlush(contract);

        BaseResponse<Object> res = new BaseResponse<>();
        res.setStatus(200);
        res.setData(Map.of("contractId", contract.getId(), "status", contract.getStatus().name()));
        res.setSuccess(true);
        res.setMessage("Contract activated");
        return res;
    }

    private String appendSignEvent(String existingJson, ObjectNode newEvent) {
        try {
            ArrayNode arr;
            if (existingJson == null || existingJson.isBlank()) {
                arr = objectMapper.createArrayNode();
            } else {
                var root = objectMapper.readTree(existingJson);
                if (root.isArray()) {
                    arr = (ArrayNode) root;
                } else {
                    arr = objectMapper.createArrayNode();
                    arr.add(root);
                }
            }
            arr.add(newEvent);
            return objectMapper.writeValueAsString(arr);
        } catch (Exception e) {
            throw new CustomBusinessException("Cannot append sign_log: " + e.getMessage());
        }
    }

    private ObjectNode buildPaperUploadedEvent(StoredContractResult stored,
                                               Long staffId,
                                               HttpServletRequest req,
                                               @Nullable String note) {
        var evt = objectMapper.createObjectNode();
        evt.put("type", "PAPER_UPLOADED");
        evt.put("at", OffsetDateTime.now(ZoneOffset.ofHours(7)).toString());
        if (staffId != null) evt.put("staff_id", staffId);

        var client = objectMapper.createObjectNode();
        client.put("ip", String.valueOf(req.getRemoteAddr()));
        client.put("ua", String.valueOf(req.getHeader("User-Agent")));
        evt.set("client", client);

        var doc = objectMapper.createObjectNode();
        doc.put("filename", stored.getFileName());
        doc.put("sha256", stored.getSha256());
        doc.put("size", stored.getSizeBytes());
        doc.put("source", "upload");
        evt.set("doc", doc);

        if (note != null && !note.isBlank()) evt.put("note", note);
        return evt;
    }

    private ObjectNode buildClientClickEvent(Long buyerId, HttpServletRequest req) {
        var evt = objectMapper.createObjectNode();
        evt.put("type", "CLIENT_CLICK");
        evt.put("at", OffsetDateTime.now(ZoneOffset.ofHours(7)).toString());
        evt.put("buyer_id", buyerId);

        var client = objectMapper.createObjectNode();
        client.put("ip", String.valueOf(req.getRemoteAddr()));
        client.put("ua", String.valueOf(req.getHeader("User-Agent")));
        evt.set("client", client);

        return evt;
    }
}
