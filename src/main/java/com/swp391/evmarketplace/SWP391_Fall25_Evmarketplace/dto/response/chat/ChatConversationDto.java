package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.chat;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.account.AccountReponseDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatConversationDto {

    private Long id;
    private AccountReponseDTO userA;
    private AccountReponseDTO userB;
    private Long listingId;
    private Long userMinId;
    private Long userMaxId;
    private Long lastMessageId;
    private LocalDateTime lastMessageAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
