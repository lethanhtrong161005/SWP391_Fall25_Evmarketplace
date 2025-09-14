package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.account;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.GoogleUserInfoDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Account;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Profile;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.AccountRole;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.AccountStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AccountServiceImp implements AccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    @Override
    public Account upsertUser(GoogleUserInfoDTO userInfo) {
        Optional<Account> optionalAccount = accountRepository.findByEmail(userInfo.getEmail());
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();

            // update account
            account.setGoogleId(userInfo.getId());
            account.setEmailVerified(Boolean.TRUE.equals(userInfo.getVerified_email()));
            account.setStatus(AccountStatus.ACTIVE);

            // update profile
            Profile profile = account.getProfile();
            if (profile == null) {
                profile = new Profile();
                profile.setAccount(account);
            }
            profile.setFullName(userInfo.getName());
            profile.setAvatarUrl(userInfo.getPicture());
            account.setProfile(profile);

            return accountRepository.save(account);
        }else{
            // create new
            Account newAccount = new Account();
            newAccount.setEmail(userInfo.getEmail());
            newAccount.setGoogleId(userInfo.getId());
            newAccount.setRole(AccountRole.MEMBER);
            newAccount.setStatus(AccountStatus.ACTIVE);
            newAccount.setEmailVerified(Boolean.TRUE.equals(userInfo.getVerified_email()));
            newAccount.setPhoneVerified(false);

            Profile profile = new Profile();
            profile.setAccount(newAccount);
            profile.setFullName(userInfo.getName());
            profile.setAvatarUrl(userInfo.getPicture());

            newAccount.setProfile(profile);

            return accountRepository.save(newAccount);
        }
    }

}
