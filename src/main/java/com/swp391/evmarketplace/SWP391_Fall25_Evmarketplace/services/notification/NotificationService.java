package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.notification;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.message.NotificationDto;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Listing;
import org.springframework.data.domain.Slice;

public interface NotificationService {
    NotificationDto sendToAccount(Long accountId, String type, String message, Long referenceId);
    BaseResponse<?> update(Long id, Long accountId);
    void sendWsOnly(String userId, NotificationDto payload);

    Slice<NotificationDto> listByAccount(Long accountId, int page, int size);
    void notifySellerAfterCommit(Listing l, String type, String msg);
}
