package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.branch;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.branch.BranchDto;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BranchServiceImp implements BranchService{
    @Autowired
    BranchRepository branchRepository;

    @Override
    public BaseResponse<List<BranchDto>> getAllList() {
        List<BranchDto> branches =  branchRepository.findAll().stream().map(item -> item.toDto(item)).toList();
        BaseResponse<List<BranchDto>> response = new BaseResponse<>();
        response.setMessage("OK");
        response.setData(branches);
        response.setSuccess(true);
        response.setStatus(200);
        return response;
    }
}
