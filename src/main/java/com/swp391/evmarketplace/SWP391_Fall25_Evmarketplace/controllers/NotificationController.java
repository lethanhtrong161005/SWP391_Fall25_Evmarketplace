package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.PageResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.message.NotificationDto;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.notification.NotificationService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private AuthUtil authUtil;

    @GetMapping
    public ResponseEntity<?> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long accountId = authUtil.getCurrentAccountIdOrNull();
        if (accountId == null) {
            return ResponseEntity.status(401).body(
                    new BaseResponse<>(401, false, "Unauthorized", null, null, LocalDateTime.now())
            );
        }
        var slice = notificationService.listByAccount(accountId, page, size);
        var body = new  PageResponse<NotificationDto>();
        body.setPage(page);
        body.setSize(size);
        body.setItems(slice.getContent());
        body.setTotalElements(slice.getNumberOfElements());
        body.setHasNext(slice.hasNext());
        body.setHasPrevious(slice.hasPrevious());

        BaseResponse<PageResponse<NotificationDto>> res = new BaseResponse<>();
        res.setSuccess(true);
        res.setStatus(200);
        res.setData(body);
        res.setMessage("Get Notifications Success");
        return ResponseEntity.ok(res);
    }

}
