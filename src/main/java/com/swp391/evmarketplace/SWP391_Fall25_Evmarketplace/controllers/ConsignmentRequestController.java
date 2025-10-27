package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.request.CancelConsignmentRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.request.CreateConsignmentRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.request.UpdateConsignmentRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.PageResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.consignment.request.ConsignmentRequestListItemDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.consignment.consignmentRequest.ConsignmentRequestService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/consignments_request")
public class ConsignmentRequestController {

    @Autowired
    private ConsignmentRequestService consignmentRequestService;
    @Autowired
    private AuthUtil authUtil;

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(
            @RequestPart("payload") String payload,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestPart(value = "videos", required = false) List<MultipartFile> videos
    ) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            CreateConsignmentRequestDTO req = objectMapper.readValue(payload, CreateConsignmentRequestDTO.class);

            var res = consignmentRequestService.createConsignmentRequest(req, images, videos);
            return ResponseEntity.status(res.getStatus()).body(res);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<BaseResponse<PageResponse<ConsignmentRequestListItemDTO>>> getMineListRequest(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String dir
    ) {
        Long id = authUtil.getCurrentAccount().getId();
        BaseResponse<PageResponse<ConsignmentRequestListItemDTO>> response = consignmentRequestService.getListByOwnerId(id, page, size, dir, sort);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/cancel")
    public ResponseEntity<BaseResponse<Void>> userCancelRequest(@Valid @RequestBody CancelConsignmentRequestDTO dto) {
        BaseResponse<Void> res = consignmentRequestService.UserCancelRequest(dto);
        return ResponseEntity.ok(res);
    }

    @PutMapping(value = "/update/{requestId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateConsignmentRequest
            (
                    @PathVariable Long requestId,
                    @RequestPart("payload") String payload,
                    @RequestPart(value = "images", required = false) List<MultipartFile> images,
                    @RequestPart(value = "videos", required = false) List<MultipartFile> videos
            ) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UpdateConsignmentRequestDTO req = objectMapper.readValue(payload, UpdateConsignmentRequestDTO.class);

            var res = consignmentRequestService.userUpdateRequest(requestId, req, images, videos);
            return ResponseEntity.status(res.getStatus()).body(res);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ConsignmentRequestListItemDTO>> getRequestById(@PathVariable("id") Long id) {
        BaseResponse<ConsignmentRequestListItemDTO> response = consignmentRequestService.getRequestById(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }


}
