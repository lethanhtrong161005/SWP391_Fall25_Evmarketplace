package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.message;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto {

    private String type;
    private String message;
    private Long referenceId;
    private LocalDateTime createdAt;
    @JsonAlias("isRead")
    private boolean read;

}
