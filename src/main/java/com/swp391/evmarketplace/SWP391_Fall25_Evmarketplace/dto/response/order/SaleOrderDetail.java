package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.order;


import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.account.AccountReponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.branch.BranchDto;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.listing.ListingDto;

public class SaleOrderDetail {
    private SaleOrderDto order;
    private AccountReponseDTO buyer;
    private AccountReponseDTO seller;
    private BranchDto branch;
    private AccountReponseDTO createdBy;
    private ListingDto listing;

}
