package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.PageResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.message.NotificationDto;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.notification.NotificationService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @RequestParam(required = false) Integer lastId,
            @RequestParam(defaultValue = "5") Integer limit
    ) {
        Long accountId = authUtil.getCurrentAccountIdOrNull();
        if (accountId == null) {
            return ResponseEntity.status(401).body(
                    new BaseResponse<>(401, false, "Unauthorized", null, null, LocalDateTime.now())
            );
        }
        var slice = notificationService.listByAccount(accountId, lastId, limit);
        var body = new  PageResponse<NotificationDto>();
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

    @PutMapping("/{id}")
    public ResponseEntity<?> updateNotification(
            @PathVariable Long id
    ){
        var res = notificationService.update(id, authUtil.getCurrentAccountIdOrNull());
        return ResponseEntity.status(res.getStatus()).body(res);
    }

}
