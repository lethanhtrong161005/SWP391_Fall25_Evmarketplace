package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.battery.CreateBatteryRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.battery.UpdateBatteryRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.vehicle.CreateVehicleRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.vehicle.UpdateVehicleRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.battery.ProductBatteryService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.vehicle.ProductVehicleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product")
public class ProductController {
    @Autowired
    private ProductVehicleService productVehicleService;
    @Autowired
    private ProductBatteryService productBatteryService;

    @GetMapping("/vehicle/all")
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String dir
    ){
        var response = productVehicleService.getAllAdaptive(page, size, sortBy, dir);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/vehicle/add")
    public ResponseEntity<?> add(@Valid @RequestBody CreateVehicleRequest request){
        var res = productVehicleService.addVehicle(request);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @PutMapping("/vehicle/update/{id}")
    public ResponseEntity<?> updatePartial(@PathVariable Long id,
                                           @Valid @RequestBody UpdateVehicleRequest req) {
        var res = productVehicleService.updateVehicle(id, req);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @GetMapping("/battery/all")
    public ResponseEntity<?> getAllAdaptive(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String dir
    ) {
        var res = productBatteryService.getAllAdaptive(page, size, sortBy, dir);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @PostMapping("/battery/add")
    public ResponseEntity<?> add(@Valid @RequestBody CreateBatteryRequest req) {
        var res = productBatteryService.addBattery(req);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @PutMapping("/battery/update/{id}")
    public ResponseEntity<?> updateBattery(@Valid @RequestBody UpdateBatteryRequest req, @PathVariable Long id) {
        var res = productBatteryService.updateBattery(id, req);
        return ResponseEntity.status(res.getStatus()).body(res);
    }


}
