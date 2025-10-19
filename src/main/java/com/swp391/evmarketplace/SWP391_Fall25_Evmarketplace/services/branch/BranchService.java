package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.branch;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.branch.BranchDto;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;

import java.util.List;

public interface BranchService {
    BaseResponse<List<BranchDto>> getAllList();
}
