package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Account;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.security.AppUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {
    public Account getCurrentAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomBusinessException("User is not authenticated");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof AppUserDetails) {
            return ((AppUserDetails) principal).getAccount();
        }
        throw new CustomBusinessException("Invalid principal type");
    }

    public Account getCurrentAccountOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;

        Object principal = auth.getPrincipal();

        if (principal instanceof AppUserDetails aud) {
            return aud.getAccount();
        }
        // String "anonymousUser"
        if (principal instanceof String s) {
            if ("anonymousUser".equalsIgnoreCase(s)) return null;
        }

        return null;
    }

    public Long getCurrentAccountIdOrNull() {
        Account acc = getCurrentAccountOrNull();
        return acc != null ? acc.getId() : null;
    }
}
