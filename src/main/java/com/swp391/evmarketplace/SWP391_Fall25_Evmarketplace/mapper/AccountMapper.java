package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.mapper;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.AccountReponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Account;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AccountMapper {

    AccountReponseDTO toAccountReponseDTO(Account account);
}
