package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.contract;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.contract.ActivateContractRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.contract.CreateContractRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.StoredContractResult;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Account;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Contract;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.SaleOrder;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ContractSignMethod;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ContractStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.OrderStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ContractRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.SaleOrderRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.file.FileService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.mapper.Mapper;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final FileService fileService;
    private final SaleOrderRepository saleOrderRepository;
    private final ObjectMapper objectMapper;



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

        if (reqDto.getEffectiveTo().isBefore(reqDto.getEffectiveFrom())) {
            throw new CustomBusinessException("EffectiveTo must be after EffectiveFrom");
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
    public BaseResponse<?> activateContract(ActivateContractRequest reqDto, HttpServletRequest http) {
        Contract contract = contractRepository.findById(reqDto.getContractId())
                .orElseThrow(() -> new CustomBusinessException("Contract not found"));

        Long buyerId = contract.getOrder().getBuyer().getId();
        if(!reqDto.getBuyerId().equals(buyerId)) {
            throw  new CustomBusinessException("Forbidden: only buyer can activate the contract");
        }

        if(contract.getStatus() == ContractStatus.CANCELLED || contract.getStatus() == ContractStatus.EXPIRED) {
            throw  new CustomBusinessException("Contract is not activatable");
        }

        if(contract.getStatus() == ContractStatus.ACTIVE){
            return new BaseResponse<Map<String, Object>>(
                    200,
                    true,
                    "Contract already active",
                    Map.of(
                            "contractId", contract.getId(),
                            "status", contract.getStatus().name()
                    ),
                    null,
                    LocalDateTime.now()
            );
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
        String newLog = appendSignEvent(contract.getSignLog(), clickEvent);
        contract.setSignLog(newLog);

        contract.setSignMethod(ContractSignMethod.CLICK);
        contract.setSignedAt(now);

        contract.setStatus(!efFrom.isAfter(now) ? ContractStatus.ACTIVE : ContractStatus.SIGNED);
        contractRepository.saveAndFlush(contract);

        BaseResponse<Object> res = new BaseResponse<>();
        res.setStatus(200);
        res.setData(Map.of(
                "contractId", contract.getId(),
                "status", contract.getStatus().name()
        ));
        res.setSuccess(true);
        res.setMessage("Contract activated");
        return res;
    }

    private String appendSignEvent(String existingJson, ObjectNode newEvent){
        try{
            ArrayNode arr;
            if(existingJson == null || existingJson.isBlank()){
                arr = objectMapper.createArrayNode();
            }else{
                var root = objectMapper.readTree(existingJson);
                if(root.isArray()){
                    arr = (ArrayNode) root;
                }else{
                    arr = objectMapper.createArrayNode();
                    arr.add(root);
                }
            }
            arr.add(newEvent);
            return objectMapper.writeValueAsString(arr);
        }catch (Exception e){
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
        evt.put("staff_id", staffId);

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
