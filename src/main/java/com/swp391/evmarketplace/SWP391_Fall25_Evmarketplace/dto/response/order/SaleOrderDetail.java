package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.order;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.account.AccountReponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.branch.BranchDto;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.listing.ListingDto;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.payment.SalePaymentDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SaleOrderDetail {
    private SaleOrderDto order;
    private ListingDto listing;
    private AccountReponseDTO buyer;
    private BranchDto branch;
    private AccountReponseDTO createdBy;
}
