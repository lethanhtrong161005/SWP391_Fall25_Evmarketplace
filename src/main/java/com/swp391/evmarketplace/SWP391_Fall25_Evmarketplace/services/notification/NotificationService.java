package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.notification;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.message.NotificationDto;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Listing;
import org.springframework.data.domain.Slice;

public interface NotificationService {
    BaseResponse<?> update(Long id, Long accountId);

    Slice<NotificationDto> listByAccount(Long accountId, int page, int size);
    void notifySellerAfterCommit(Listing l, String type, String title, String msg);

    void notifyUserAfterCommit(Long accountId, Long referenceId, String type, String title, String msg);
}
