package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.notification;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.message.NotificationDto;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Listing;
import org.springframework.data.domain.Slice;

public interface NotificationService {
    /** Lưu DB + push qua WebSocket tới accountId */
    NotificationDto sendToAccount(Long accountId, String type, String message, Long referenceId);

    /** Chỉ push WS (không lưu DB) tới userId (Principal name) */
    void sendWsOnly(String userId, NotificationDto payload);

    /** Danh sách thông báo theo account (Slice) */
    Slice<NotificationDto> listByAccount(Long accountId, int page, int size);
    void notifySellerAfterCommit(Listing l, String type, String msg);
}
