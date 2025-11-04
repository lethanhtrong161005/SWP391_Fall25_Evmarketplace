package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.consignmentPayout;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignmentPayout.ConsignmentPayoutCreateRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.consignmentPayout.ConsignmentPayoutResponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.StoredContractResult;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ConsignmentPayout;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ConsignmentPayoutRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.file.FileService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.AuthUtil;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class consignmentPayoutServiceImp {

    @Autowired
    ConsignmentPayoutRepository consignmentPayoutRepository;
    @Autowired
    AuthUtil authUtil;
    @Autowired
    FileService fileService;

    @Transactional
    public ConsignmentPayoutResponseDTO create(ConsignmentPayoutCreateRequestDTO req, MultipartFile file) {

        if (consignmentPayoutRepository.existsBySettlementId(req.getSettlementId())) {
            throw new EntityExistsException("Payout for this settlement already exists");
        }

        Long currentUserId = authUtil.getCurrentAccount().getId();

        //file
        StoredContractResult result;
        try {
            result = fileService.storedContract(file);
        } catch (IOException e) {
            throw new CustomBusinessException("Error while uploading file: " + e.getMessage());
        }

        ConsignmentPayout entity = ConsignmentPayout.builder()
                .settlementId(req.getSettlementId())
                .agreementId(req.getAgreementId())
                .ownerId(req.getOwnerId())
                .paidAmount(req.getPaidAmount())
                .method(req.getMethod())
                .note(req.getNote())
                .medialUrl(result.getFileName())
                .recordedBy(currentUserId)
                .paidAt(req.getPaidAt())
                .build();

        entity = consignmentPayoutRepository.save(entity);
        return toResponse(entity);
    }

    @Transactional(readOnly = true)
    public ConsignmentPayoutResponseDTO showById(Long id) {
        ConsignmentPayout entity = consignmentPayoutRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payout not found: id = " + id));
        return toResponse(entity);
    }

    private ConsignmentPayoutResponseDTO toResponse(ConsignmentPayout e) {
        return new ConsignmentPayoutResponseDTO(
                e.getId(),
                e.getSettlementId(),
                e.getAgreementId(),
                e.getOwnerId(),
                e.getPaidAmount(),
                e.getMethod(),
                e.getNote(),
                e.getRecordedBy(),
                e.getPaidAt(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }



}
