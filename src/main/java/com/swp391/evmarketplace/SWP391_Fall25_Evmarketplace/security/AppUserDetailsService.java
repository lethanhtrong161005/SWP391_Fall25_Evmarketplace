package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.security;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Account;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        Account account;

        if (identifier.startsWith("google_")) {
            String googleId = identifier.substring(7);
            account = accountRepository.findByGoogleId(googleId)
                    .orElseThrow(() -> new UsernameNotFoundException("Google account not found"));
        } else if (identifier.startsWith("phone_")) {
            String phone = identifier.substring(6);
            account = accountRepository.findByPhoneNumber(phone)
                    .orElseThrow(() -> new UsernameNotFoundException("Phone account not found"));
        } else {
            throw new UsernameNotFoundException("Unknown identifier type: " + identifier);
        }

        return new AppUserDetails(account);
    }

}
