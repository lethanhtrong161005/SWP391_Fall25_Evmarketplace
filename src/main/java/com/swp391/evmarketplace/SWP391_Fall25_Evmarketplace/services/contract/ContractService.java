package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.contract;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.contract.ActivateContractRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.contract.CreateContractRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public interface ContractService {

    /**
     *
     * */
    BaseResponse<?> createContract(
            CreateContractRequest reqDto,
            MultipartFile contractFile,
            HttpServletRequest http
    );


    BaseResponse<?> activateContract(ActivateContractRequest reqDto,
                                    HttpServletRequest http);


}
