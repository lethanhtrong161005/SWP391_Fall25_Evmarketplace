package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.message;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ChatMessageType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDto {
    private Long id;
    private Long conversationId;
    private Long senderId;
    private Long recipientId;
    private ChatMessageType type;
    private String textContent;
    private String mediaUrl;
    private LocalDateTime createdAt;
    private LocalDateTime seenAt;
}
