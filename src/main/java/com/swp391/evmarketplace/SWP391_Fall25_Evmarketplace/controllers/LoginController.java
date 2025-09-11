package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    @PostMapping("/local-login")
    public ResponseEntity<String> localLogin() throws Exception {
        return ResponseEntity.ok("local-login");
    }

}
