package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.chat;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.StoredFile;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.message.ChatMessageDto;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ChatConversation;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ChatMessage;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ChatMessageType;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ChatConversationRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ChatMessageRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.file.FileService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.MedialUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatConversationRepository conversationRepo;
    private final ChatMessageRepository messageRepo;
    private final SimpMessagingTemplate messaging;
    private final FileService fileService;

    private static final String CHAT_QUEUE = "/queue/chat/";


    @Value("${server.url}")
    private String serverUrl;

    @Override
    @Transactional
    public ChatConversation openOrGetConversation(Long me, Long other, Long listingId) {
        long min = Math.min(me, other);
        long max = Math.max(me, other);

        return conversationRepo.findByUserMinIdAndUserMaxId(min, max)
                .orElseGet(() -> {
                    ChatConversation c = new ChatConversation();
                    c.setUserAId(me);
                    c.setUserBId(other);
                    c.setListingId(listingId);
                    c.setUserMinId(min);
                    c.setUserMaxId(max);
                    try {
                        return conversationRepo.save(c);
                    } catch (DataIntegrityViolationException e) {
                        // cặp đã được tạo bởi req khác cùng lúc → lấy lại
                        return conversationRepo.findByUserMinIdAndUserMaxId(min, max).orElseThrow(() -> e);
                    }
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChatConversation> listConversations(Long me, Pageable pageable) {
        return conversationRepo.findConversationsOf(me, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChatMessageDto> listMessages(Long conversationId, Long me, Pageable pageable) {
        ensureMember(conversationId, me);
        return messageRepo.findByConversationIdOrderByCreatedAtDesc(conversationId, pageable)
                .map(m -> {
                    ChatMessageDto dto = m.toDto(m);
                    if (m.getType() == ChatMessageType.IMAGE) {
                        dto.setMediaUrl(MedialUtils.converMediaNametoMedialUrl(m.getMediaUrl(), "IMAGE", serverUrl));
                    } else if (m.getType() == ChatMessageType.VIDEO) {
                        dto.setMediaUrl(MedialUtils.converMediaNametoMedialUrl(m.getMediaUrl(), "VIDEO", serverUrl));
                    }
                    return dto;
                });
    }

    @Override
    @Transactional
    public ChatMessageDto sendMessage(Long conversationId, Long senderId, Long recipientId,
                                   ChatMessageType type, String text) {
        ensureMember(conversationId, senderId);
        ensureRecipient(conversationId, recipientId);

        if (type != null && type != ChatMessageType.TEXT) {
            throw new CustomBusinessException("Invalid type for text message. Use TEXT.");
        }
        if (!StringUtils.hasText(text)) {
            throw new CustomBusinessException("Message text cannot be empty");
        }

        ChatMessage toSave = ChatMessage.builder()
                .conversationId(conversationId)
                .senderId(senderId)
                .recipientId(recipientId)
                .type(ChatMessageType.TEXT)
                .textContent(text)
                .mediaUrl(null)
                .build();

        final ChatMessage saved = messageRepo.save(toSave);

        try {
            final Long lastId = saved.getId();
            final LocalDateTime lastAt = saved.getCreatedAt();
            conversationRepo.findById(conversationId).ifPresent(conv -> {
                conv.setLastMessageId(lastId);
                conv.setLastMessageAt(lastAt);
            });
        } catch (Exception ignore) {}

        ChatMessageDto dto = saved.toDto(saved);
        messaging.convertAndSendToUser(recipientId.toString(), CHAT_QUEUE + conversationId, dto);
        messaging.convertAndSendToUser(senderId.toString(),    CHAT_QUEUE + conversationId, dto);

        return dto;
    }


    @Override
    @Transactional
    public ChatMessageDto sendMedia(Long conversationId, Long senderId, Long recipientId,
                                 ChatMessageType type, MultipartFile file) {
        ensureMember(conversationId, senderId);
        ensureRecipient(conversationId, recipientId);

        if (type == null || (type != ChatMessageType.IMAGE && type != ChatMessageType.VIDEO)) {
            throw new CustomBusinessException("You need to specify IMAGE or VIDEO");
        }

        final StoredFile stored;
        try {
            stored = (type == ChatMessageType.IMAGE)
                    ? fileService.storeImage(file)
                    : fileService.storeVideo(file);
        } catch (Exception e) {
            log.error("Store media error", e);
            throw new CustomBusinessException("Error while storing file");
        }

        ChatMessage toSave = ChatMessage.builder()
                .conversationId(conversationId)
                .senderId(senderId)
                .recipientId(recipientId)
                .type(type)
                .mediaUrl(stored.getStoredName())
                .build();

        final ChatMessage saved = messageRepo.save(toSave); // <-- dùng biến mới, final

        // Fallback update last_message_*
        try {
            final Long lastId = saved.getId();
            final LocalDateTime lastAt = saved.getCreatedAt();
            conversationRepo.findById(conversationId).ifPresent(conv -> {
                conv.setLastMessageId(lastId);
                conv.setLastMessageAt(lastAt);
            });
        } catch (Exception ignore) {}

        ChatMessageDto dto = saved.toDto(saved);
        if (saved.getType() == ChatMessageType.IMAGE) {
            dto.setMediaUrl(MedialUtils.converMediaNametoMedialUrl(saved.getMediaUrl(), "IMAGE", serverUrl));
        } else if (saved.getType() == ChatMessageType.VIDEO) {
            dto.setMediaUrl(MedialUtils.converMediaNametoMedialUrl(saved.getMediaUrl(), "VIDEO", serverUrl));
        }
        messaging.convertAndSendToUser(recipientId.toString(), CHAT_QUEUE + conversationId, dto);
        messaging.convertAndSendToUser(senderId.toString(),    CHAT_QUEUE + conversationId, dto);

        return dto;
    }


    @Override
    @Transactional
    public int markSeen(Long conversationId, Long viewerId) {
        ensureMember(conversationId, viewerId);
        return messageRepo.markSeenInConversation(conversationId, viewerId, LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public long unreadCount(Long me) {
        return messageRepo.countByRecipientIdAndSeenAtIsNull(me);
    }

    private void ensureMember(Long conversationId, Long uid) {
        if (!conversationRepo.isMember(conversationId, uid)) {
            throw new CustomBusinessException("User is not a member of this conversation");
        }
    }
    private void ensureRecipient(Long conversationId, Long recipientId) {
        ChatConversation c = conversationRepo.findById(conversationId)
                .orElseThrow(() -> new CustomBusinessException("Conversation not found"));
        if (!(recipientId.equals(c.getUserAId()) || recipientId.equals(c.getUserBId()))) {
            throw new CustomBusinessException("Recipient is not a member of this conversation");
        }
    }

}
