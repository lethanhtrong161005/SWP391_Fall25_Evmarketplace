package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.chat;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.message.ChatMessageDto;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ChatConversation;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ChatMessage;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ChatMessageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ChatService {
    ChatConversation openOrGetConversation(Long me, Long other, Long listingId);
    Page<ChatConversation> listConversations(Long me, Pageable pageable);
    Page<ChatMessageDto> listMessages(Long conversationId, Long me, Pageable pageable);
    ChatMessageDto sendMessage(Long conversationId,
                            Long senderId,
                            Long recipientId,
                            ChatMessageType type,
                            String text );
    int markSeen(Long conversationId, Long viewerId);
    long unreadCount(Long me);
    ChatMessageDto sendMedia(Long conversationId, Long senderId, Long recipientId,
                             ChatMessageType type, MultipartFile file);
}
