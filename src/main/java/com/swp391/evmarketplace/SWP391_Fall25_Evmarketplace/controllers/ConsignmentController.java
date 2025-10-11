package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.request.CreateConsignmentRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.request.UpdateSetScheduleRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.PageResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Account;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections.ConsignmentRequestProject;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.consignment.ConsignmentRequestService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/consignments")
public class ConsignmentController {

    @Autowired
    private ConsignmentRequestService consignmentRequestService;
    @Autowired
    private AuthUtil authUtil;

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(
            @RequestPart("payload") String payload,
            @RequestPart(value="images", required=false) List<MultipartFile> images,
            @RequestPart(value="videos", required=false) List<MultipartFile> videos
    ){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            CreateConsignmentRequestDTO req = objectMapper.readValue(payload, CreateConsignmentRequestDTO.class);

            Account account = authUtil.getCurrentAccount();
            var res = consignmentRequestService.createConsignmentRequest(req, account, images, videos);
            return ResponseEntity.status(res.getStatus()).body(res);
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<BaseResponse<PageResponse<ConsignmentRequestProject>>> getMineListRequest(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String dir
    ){
        Long id = authUtil.getCurrentAccount().getId();
        BaseResponse<PageResponse<ConsignmentRequestProject>> response = consignmentRequestService.getListById( id, page, size, dir, sort);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
