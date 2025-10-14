package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.favorite;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;

public interface FavoriteService {
    BaseResponse<?> addFavorite(Long userId, Long listingId);

    BaseResponse<?> removeFavorite(Long userId, Long listingId);

    BaseResponse<?> getFavoriteByAccount(Long accountId);
}
