package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.notification;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.message.NotificationDto;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Account;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Listing;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Notification;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.AccountRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.NotificationRepository;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final String USER_QUEUE = "/queue/notifications";

    private final SimpMessagingTemplate messaging;
    private final NotificationRepository notificationRepository;
    private final AccountRepository accountRepository;


    @Override
    @Transactional
    public NotificationDto sendToAccount(Long accountId, String type, String message, Long referenceId) {

        Account acc = accountRepository.findById(accountId)
                .orElseThrow(() -> new CustomBusinessException("Account not found: " + accountId));


        Notification n = new Notification();
        n.setType(type);
        n.setMessage(message);
        n.setReferenceId(referenceId);
        n.setIsRead(false);
        n.setAccount(acc);
        Notification saved = notificationRepository.save(n);

        NotificationDto dto = saved.toDto(saved);

        messaging.convertAndSendToUser(String.valueOf(accountId), USER_QUEUE, dto);

        log.debug("Pushed notification to user {} at {} -> {}", accountId, USER_QUEUE, dto.getType());
        return dto;
    }

    @Override
    public void sendWsOnly(String userId, NotificationDto payload) {
        messaging.convertAndSendToUser(userId, USER_QUEUE, payload);
        log.debug("Pushed WS-only notification to user {} -> {}", userId, payload.getType());
    }


    @Override
    @Transactional(readOnly = true)
    public Slice<NotificationDto> listByAccount(Long accountId, int page, int size) {
        return notificationRepository
                .findByAccount_IdOrderByCreatedAtDesc(accountId, PageRequest.of(page, size))
                .map(n -> n.toDto(n));
    }

    public void notifySellerAfterCommit(Listing l, String type, String msg) {
        if (l == null || l.getSeller() == null) return;
        final Long sellerId = l.getSeller().getId();
        final Long listingId = l.getId();

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    sendToAccount(sellerId, type, msg, listingId);
                } catch (Exception ex) {
                    log.warn("Failed to push notification to user {} for listing {}: {}", sellerId, listingId, ex.getMessage());
                    throw  new CustomBusinessException(ex.getMessage());
                }
            }
        });
    }

    @Override
    public BaseResponse<?> update(Long id, Long accountId) {
        Notification n =  notificationRepository.findById(id)
                .orElseThrow(() -> new CustomBusinessException("Notification not found: " + id));
        if(!n.getAccount().getId().equals(accountId)){
            throw new CustomBusinessException("Notification account not found: " + accountId);
        }
        if(n.getIsRead()){
            throw new CustomBusinessException("Notification is already read");
        }
        n.setIsRead(true);
        notificationRepository.save(n);
        BaseResponse<?> response = new BaseResponse();
        response.setMessage("Notification updated");
        response.setSuccess(true);
        response.setStatus(200);
        return response;
    }

}
