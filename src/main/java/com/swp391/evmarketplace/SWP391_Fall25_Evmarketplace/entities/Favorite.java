package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.favorite.FavoriteDto;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.MediaType;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.MedialUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "favorite")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne( optional = false)
    @JoinColumn(name = "account_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_fav_acc"))
    private Account account;

    @ManyToOne(optional = false)
    @JoinColumn(name = "listing_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_fav_listing"))
    private Listing listing;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public FavoriteDto toDto(Favorite favorite, String serverUrl) {
        FavoriteDto favoriteDto = new FavoriteDto();
        favoriteDto.setId(favorite.getId());
        favoriteDto.setListingId(favorite.getListing().getId());
        favoriteDto.setTitle(favorite.getListing().getTitle());
        favoriteDto.setPrice(favorite.getListing().getPrice());
        favoriteDto.setProvince(favorite.getListing().getProvince());
        favoriteDto.setAddress(favorite.getListing().getAddress());
        favoriteDto.setWard(favorite.getListing().getWard());
        favoriteDto.setDistrict(favorite.getListing().getDistrict());
        favoriteDto.setVisibility(favorite.getListing().getVisibility());
        String thumbnailUrl = "";
        for(ListingMedia l : favorite.getListing().getMediaList()){
            if(l.getMediaType() == MediaType.IMAGE){
                thumbnailUrl = l.getMediaUrl();
                break;
            }
        }
        favoriteDto.setThumbnailUrl(MedialUtils.converMediaNametoMedialUrl(thumbnailUrl, MediaType.IMAGE.name(), serverUrl));
        favoriteDto.setFavoredAt(favorite.getCreatedAt());
        favoriteDto.setConsigned(favorite.getListing().getConsigned());
        favoriteDto.setTimeAgo(favorite.getListing().getCreatedAt());
        return favoriteDto;
    }


}
