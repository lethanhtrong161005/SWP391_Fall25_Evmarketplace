package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.model.CreateModelRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.model.UpdateModelRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.model.ModelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/model")
public class ModelController {
    @Autowired
    private ModelService modelService;

    @GetMapping("/all")
    public ResponseEntity<?> getAll(){
        var res = modelService.getAllModels();
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addModel(@Valid @RequestBody CreateModelRequest request){
        var res = modelService.addModel(request);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateModel(@RequestBody UpdateModelRequest request, @PathVariable long id){
        var res = modelService.updateModel(request, id);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteModel(@PathVariable long id){
        var res = modelService.deleteModel(id);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

}
