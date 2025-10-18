package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.mapper;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.account.AccountReponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AccountMapper {

    @Mapping(target = "branch", ignore = true)
    AccountReponseDTO toAccountReponseDTO(Account account);
}
