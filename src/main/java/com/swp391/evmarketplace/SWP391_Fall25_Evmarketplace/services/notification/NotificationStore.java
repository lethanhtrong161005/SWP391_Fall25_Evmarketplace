package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.notification;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Account;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Notification;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.AccountRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationStore {

    private final NotificationRepository notificationRepository;
    private final AccountRepository accountRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public Notification save(Long accountId, String type, String title, String message, Long referenceId) {
        Account acc = accountRepository.findById(accountId)
                .orElseThrow(() -> new CustomBusinessException("Account not found: " + accountId));

        Notification n = new Notification();
        n.setAccount(acc);
        n.setType(type);
        n.setTitle(title);
        n.setMessage(message);
        n.setReferenceId(referenceId);
        n.setIsRead(false);

        return notificationRepository.save(n);
    }
}
