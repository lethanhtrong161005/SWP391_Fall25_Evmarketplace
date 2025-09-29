package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.account.CreateStaffAccountRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.account.AccountReponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.account.StaffAccountResponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.account.AccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/admin")
@Tag(name = "admin", description = "APIs that admin manage")
public class AdminController {
    @Autowired
    AccountService accountService;

    @GetMapping("/accounts/")
    public ResponseEntity<BaseResponse<Map<String, Object>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false, defaultValue = "desc") String dir
    ) {
        BaseResponse<Map<String, Object>> response = accountService.getAll(page, size, sort, dir);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // GET /api/accounts/searchAccount?keyword=John&page=0&size=10
    @GetMapping("/accounts/searchAccount")
    public ResponseEntity<BaseResponse<Map<String, Object>>> searchAccount(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false, defaultValue = "asc") String dir
    ) {
        BaseResponse<Map<String, Object>> response = accountService.search(keyword, page, size, sort, dir);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/accounts/{id}")
    public ResponseEntity<BaseResponse<AccountReponseDTO>> getAccountById(@PathVariable Long id){
        BaseResponse<AccountReponseDTO> reponseDTO = accountService.getAccountById(id);
        return ResponseEntity.status(reponseDTO.getStatus()).body(reponseDTO);
    }

    @PatchMapping("/accounts/{id}/block")
    public ResponseEntity<Void> blockAccount(@PathVariable Long id) {
        accountService.blockAccount(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/accounts/{id}/unblock")
    public ResponseEntity<Void> unblockAccount(@PathVariable Long id) {
        accountService.unblockAccount(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/accounts/register")
    public ResponseEntity<BaseResponse<StaffAccountResponseDTO>> createStaffAccount(CreateStaffAccountRequestDTO requestDTO) {
        BaseResponse<StaffAccountResponseDTO> responseDTO = accountService.createStaffAccount(requestDTO);
        return ResponseEntity.status(responseDTO.getStatus()).body(responseDTO);
    }
}
