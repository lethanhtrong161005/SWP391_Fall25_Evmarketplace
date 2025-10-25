package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.message.ChatMessageDto;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ChatMessageType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "chat_message",
        indexes = {
                @Index(name = "idx_msg_conv_time", columnList = "conversation_id,created_at"),
                @Index(name = "idx_msg_unread", columnList = "recipient_id,seen_at"),
                @Index(name = "idx_msg_rcpt_conv_unseen", columnList = "recipient_id,conversation_id,seen_at"),
                @Index(name = "idx_msg_sender_time", columnList = "sender_id,created_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conversation_id", nullable = false)
    private Long conversationId;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "recipient_id", nullable = false)
    private Long recipientId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, columnDefinition = "ENUM('TEXT','IMAGE','VIDEO','SYSTEM')")
    private ChatMessageType type = ChatMessageType.TEXT;

    @Column(name = "text_content", columnDefinition = "TEXT")
    private String textContent;

    @Column(name = "media_url", length = 500)
    private String mediaUrl;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "seen_at")
    private LocalDateTime seenAt;

    public ChatMessageDto toDto(ChatMessage cm) {
            ChatMessageDto dto = new ChatMessageDto();
            dto.setId(cm.getId());
            dto.setConversationId(cm.getConversationId());
            dto.setSenderId(cm.getSenderId());
            dto.setRecipientId(cm.getRecipientId());
            dto.setType(cm.getType());
            dto.setTextContent(cm.getTextContent());
            dto.setMediaUrl(cm.getMediaUrl());
            dto.setCreatedAt(cm.getCreatedAt());
            dto.setSeenAt(cm.getSeenAt());
            return dto;
    }

}
