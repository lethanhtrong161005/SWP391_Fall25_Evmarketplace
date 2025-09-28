package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.listing;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.listing.ListingReponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ListingMedia;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.MediaType;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListingServiceImp implements ListingService {

    @Autowired
    private ListingRepository listingRepository;
    @Value("${server.url}")
    private String serverUrl;


    @Override
   public BaseResponse<List<ListingReponseDTO>> getAllListings(int pageSize, int pageNumber) {
        Pageable page = PageRequest.of(pageNumber, pageSize);
        List<ListingReponseDTO> listings = listingRepository.findAll(page)
                .stream().map(listing -> {
                    ListingReponseDTO dto = new ListingReponseDTO();
                    dto.setId(listing.getId());
                    dto.setTitle(listing.getTitle());
                    dto.setProductVehicleId(
                            listing.getProductVehicle() != null ? listing.getProductVehicle().getId() : null
                    );
                    dto.setProductBatteryId(
                            listing.getProductBattery() != null ? listing.getProductBattery().getId() : null
                    );
                    dto.setSellerId(listing.getSeller().getId());


                    dto.setBrand(listing.getBrand());
                    dto.setModel(listing.getModel());
                    dto.setYear(listing.getYear());

                    dto.setBatteryCapacityKwh(listing.getBatteryCapacityKwh());
                    dto.setSohPercent(listing.getSohPercent());
                    dto.setMileageKm(listing.getMileageKm());
                    dto.setColor(listing.getColor());
                    dto.setDescription(listing.getDescription());


                    dto.setPrice(listing.getPrice());
                    dto.setVerified(listing.getVerified());
                    dto.setStatus(listing.getStatus().name());
                    dto.setProvince(listing.getProvince());
                    dto.setCity(listing.getCity());
                    dto.setAddress(listing.getAddress());
                    dto.setPromotedUntil(listing.getPromotedUntil());
                    String thumbnail = "";
                    if( listing.getMediaList() != null && !listing.getMediaList().isEmpty()){
                        for(ListingMedia l : listing.getMediaList()){
                            if(l.getMediaType() == MediaType.IMAGE){
                                thumbnail = l.getMediaUrl();
                                break;
                            }
                        }
                    }
                    thumbnail = serverUrl + "/api/files/images/" + thumbnail;
                    if(!thumbnail.isEmpty()){
                        dto.setThumbnail(thumbnail);
                    }
                    dto.setBranchId(
                            listing.getBranch() != null ? listing.getBranch().getId() : null
                    );
                    dto.setConsigned(listing.getConsigned());
                    dto.setCreatedAt(listing.getCreatedAt());
                    dto.setUpdatedAt(listing.getUpdatedAt());
                    dto.setAddress(listing.getAddress());
                    return dto;
                }).toList();
        BaseResponse<List<ListingReponseDTO>> response = new BaseResponse<>();
        if(listings.isEmpty()) {
            throw new CustomBusinessException("No listings found");
        }
        response.setData(listings);
        response.setStatus(200);
        response.setSuccess(true);
        response.setMessage("Get all listings");
        return response;
    }

//    @Override
//    public BaseResponse<ListingDetailResponseDTO> getListingById(long id) {
//        Optional<Listing> listing = listingRepository.findById(id);
//        if(listing.isPresent()) {
//            ListingDetailResponseDTO dto = new ListingDetailResponseDTO();
//
//        }
//
//    }

}
