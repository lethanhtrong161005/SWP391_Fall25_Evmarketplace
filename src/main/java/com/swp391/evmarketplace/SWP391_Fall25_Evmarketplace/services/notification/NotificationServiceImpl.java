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
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private static final String USER_QUEUE = "/queue/notifications";

    private final SimpMessagingTemplate messaging;
    private final NotificationStore store;
    private final NotificationRepository notificationRepository;



    @Override
    @Transactional(readOnly = true)
    public Slice<NotificationDto> listByAccount(Long accountId, int page, int size) {
        return notificationRepository
                .findByAccount_IdOrderByCreatedAtDesc(accountId, PageRequest.of(page, size))
                .map(n -> n.toDto(n));
    }

    public void notifySellerAfterCommit(Listing l, String type, String title, String msg) {
        if (l == null || l.getSeller() == null) return;
        final Long sellerId = l.getSeller().getId();
        final Long listingId = l.getId();

        Notification saved = store.save(sellerId, type, title, msg, listingId);
        NotificationDto dto = saved.toDto(saved);

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override public void afterCommit() {
                    try {
                        messaging.convertAndSendToUser(String.valueOf(sellerId), USER_QUEUE, dto);
                        log.debug("WS pushed (afterCommit) to seller {} for listing {}", sellerId, listingId);
                    } catch (Exception ex) {
                        log.warn("WS push failed afterCommit: {}", ex.getMessage());
                    }
                }
            });
        } else {
            messaging.convertAndSendToUser(String.valueOf(sellerId), USER_QUEUE, dto);
        }
    }

    @Override
    @Transactional
    public BaseResponse<?> update(Long id, Long accountId) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new CustomBusinessException("Notification not found: " + id));
        if (!n.getAccount().getId().equals(accountId)) {
            throw new CustomBusinessException("Notification account not found: " + accountId);
        }
        if (Boolean.TRUE.equals(n.getIsRead())) {
            throw new CustomBusinessException("Notification is already read");
        }
        n.setIsRead(true);

        BaseResponse<?> res = new BaseResponse<>();
        res.setMessage("Notification updated");
        res.setSuccess(true);
        res.setStatus(200);
        return res;
    }
}

