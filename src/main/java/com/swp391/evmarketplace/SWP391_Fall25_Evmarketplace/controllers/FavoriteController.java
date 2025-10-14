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

    @PutMapping("/me/add/{listingId}")
    public ResponseEntity<?> addFavorite(@PathVariable Long listingId) {
        var res = favoriteService.addFavorite(authUtil.getCurrentAccount().getId(), listingId);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @DeleteMapping("/me/delete/{listingId}")
    public ResponseEntity<?> deleteFavorite(@PathVariable Long listingId) {
        var res = favoriteService.removeFavorite(authUtil.getCurrentAccount().getId(), listingId);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @GetMapping("/me/all")
    public ResponseEntity<?> getAllFavorites() {
        return ResponseEntity.ok("done");
    }


}
