package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Notification;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationDto {
    private String type;
    private String message;
    private Long referenceId;
    private LocalDateTime createdAt;

    @JsonProperty("isRead")
    private boolean read;


}
