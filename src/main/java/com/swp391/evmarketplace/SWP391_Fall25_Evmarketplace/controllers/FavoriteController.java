package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.favorite.FavoriteService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;
    @Autowired
    private AuthUtil authUtil;

    @PostMapping("/{id}")
    public ResponseEntity<?> addFavorite(@PathVariable Long id) {
        var res = favoriteService.addFavorite(authUtil.getCurrentAccount().getId(), id);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFavorite(@PathVariable Long id) {
        var res = favoriteService.removeFavorite(authUtil.getCurrentAccount().getId(), id);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @GetMapping
    public ResponseEntity<?> getAllFavorite(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        var res = favoriteService.getFavoriteByAccount(authUtil.getCurrentAccountIdOrNull(), page, size);
        return ResponseEntity.status(res.getStatus()).body(res);
    }


}
