package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.listing;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.account.AccountReponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.battery.BatteryListResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.branch.BranchDto;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.vehicle.VehicleListReponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Branch;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ListingMedia;

import lombok.Data;

import java.util.List;

@Data
public class ListingDetailResponseDto {
    private ListingDto listing;
    private AccountReponseDTO sellerId;
    private BranchDto branch;
    private VehicleListReponse productVehicle;
    private BatteryListResponse productBattery;
    private List<ListingMediaDto> media;
}
