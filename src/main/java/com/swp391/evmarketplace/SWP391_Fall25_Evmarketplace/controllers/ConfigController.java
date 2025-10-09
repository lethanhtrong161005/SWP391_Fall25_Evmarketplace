package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.config.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/config")
public class ConfigController {
    @Autowired
    private ConfigService configService;

    @GetMapping("/listing/fee")
    public ResponseEntity<?> getConfig(){
        var res = configService.getFeeListingConfig();
        return ResponseEntity.status(res.getStatus()).body(res);
    }

}
