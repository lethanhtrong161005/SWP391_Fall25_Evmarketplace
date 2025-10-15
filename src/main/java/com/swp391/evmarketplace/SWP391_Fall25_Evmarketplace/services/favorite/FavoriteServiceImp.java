package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.favorite;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.favorite.FavoriteDto;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Account;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Favorite;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Listing;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ListingStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.AccountRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.FavoriteRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ListingRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImp implements FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private ListingRepository listingRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Value("${server.url}")
    private String serverUrl;

    //Giới hạn tối đa sô lượng bài đăng được yêu thích
    private static final int FAVORITE_LIMIT = 50;

    @Override
    @Transactional
    public BaseResponse<?> addFavorite(Long userId, Long listingId) {
        if(favoriteRepository.existsByAccount_IdAndListing_Id(userId, listingId)) {
            throw new CustomBusinessException("Favorite already exists");
        }
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new CustomBusinessException("Listing not found"));
        if(listing.getStatus() != ListingStatus.ACTIVE){
            throw new CustomBusinessException("Listing is not active");
        }

        Account account = accountRepository.lockById(userId).orElseThrow(() -> new CustomBusinessException("Account not found"));

        long used = favoriteRepository.countByAccount_Id(account.getId());
        if(used >= FAVORITE_LIMIT){
            throw new CustomBusinessException("Favorite limit exceeded");
        }

        try{
            Favorite favorite = new Favorite();
            favorite.setAccount(account);
            favorite.setListing(listing);
            favoriteRepository.save(favorite);
            BaseResponse<?> response = new BaseResponse<>();
            response.setSuccess(true);
            response.setMessage("Successfully added favorite");
            response.setStatus(200);
            return response;
        }catch (Exception e){
            throw new CustomBusinessException("Failed to add favorite: " + e.getMessage());
        }
        
    }

    @Override
    @Transactional
    public BaseResponse<?> removeFavorite(Long userId, Long listingId) {
        int deleted = favoriteRepository.deleteByAccount_IdAndListing_Id(userId, listingId);
        if (deleted == 0) {
            throw new CustomBusinessException("Favorite not found");
        }
        BaseResponse<?> response = new BaseResponse<>();
        response.setSuccess(true);
        response.setMessage("Successfully removed favorite");
        response.setStatus(200);
        return response;
    }

    @Override
    @Transactional
    public BaseResponse<?> getFavoriteByAccount(Long accountId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Slice<Favorite> raw = favoriteRepository
                .findByAccount_IdOrderByCreatedAtDesc(accountId, pageable);

        List<FavoriteDto> items = raw.getContent().stream()
                .map(f -> f.toDto(f, serverUrl))
                .toList();

        Slice<FavoriteDto> slice = new SliceImpl<>(items, pageable, raw.hasNext());

        Map<String, Object> payload = Map.of(
                "items", slice.getContent(),
                "page", page,
                "size", size,
                "hasNext", slice.hasNext()
        );

        BaseResponse<Map<String, Object>> response = new BaseResponse<>();
        response.setSuccess(true);
        response.setMessage("Successfully retrieved favorites");
        response.setStatus(200);
        response.setData(payload);
        return response;
    }

}
