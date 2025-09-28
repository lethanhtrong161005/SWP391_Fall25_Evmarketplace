package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.account.CreateStaffAccountRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.StaffAccountResponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Account;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.account.AccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/admin")
@Tag(name = "admin", description = "APIs that admin manage")
public class AdminController {
    @Autowired
    AccountService accountService;

    @GetMapping("/accounts/")
    public ResponseEntity<List<Account>> listAllAccount() {
        List<Account> accounts = accountService.getAllAccounts();
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @GetMapping("/accounts/search")
    public ResponseEntity<List<Account>> searchAccountByKeyword(String keyword) {
        List<Account> accounts = accountService.searchAccountByName(keyword);
        return new ResponseEntity<>(accounts, HttpStatus.OK);
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
    public ResponseEntity<?> createStaffAccount(CreateStaffAccountRequestDTO requestDTO){
        BaseResponse<StaffAccountResponseDTO> responseDTO = accountService.createStaffAccount(requestDTO);
        return ResponseEntity.status(responseDTO.getStatus()).body(responseDTO);
    }
}
