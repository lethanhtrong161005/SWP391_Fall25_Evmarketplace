package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.AccountReponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Account;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.mapper.AccountMapper;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.AuthUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
@Tag(name = "Accounts", description = "Account APIs")
public class AccountController {
    @Autowired
    private AuthUtil authUtil;
    @Autowired
    private AccountMapper accountMapper;

    @GetMapping("/current")
    public ResponseEntity<?> getAccountDetails() {
        BaseResponse<AccountReponseDTO> response = new BaseResponse<>();
        Account ac = authUtil.getCurrentAccount();
        if (ac != null) {
            AccountReponseDTO accountReponseDTO = accountMapper.toAccountReponseDTO(ac);
            response.setData(accountReponseDTO);
            response.setMessage("Get Account Success");
            response.setSuccess(true);
            response.setStatus(200);
        }else{
            response.setMessage("Get Account Failed");
            response.setSuccess(false);
            response.setStatus(400);
        }
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
