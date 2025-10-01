package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.brand.CreateBrandRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Brand;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.brand.BrandService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllBrands() {
        var res = brandService.getAllBrand();
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addBrand(@RequestBody @Valid CreateBrandRequest request) {
        var res = brandService.addBrand(request);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteBrand(@PathVariable Long id) {
        var res = brandService.deleteBrand(id);
        return ResponseEntity.status(res.getStatus()).body(res);
    }


}
