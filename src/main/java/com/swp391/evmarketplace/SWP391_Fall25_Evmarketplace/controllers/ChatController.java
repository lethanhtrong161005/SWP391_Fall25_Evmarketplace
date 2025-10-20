package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.message.ChatMessageDto;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ChatConversation;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ChatMessage;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ChatMessageType;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.chat.ChatService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    @Autowired
    private ChatService chatService;
    @Value("${server.url}")
    private String serverUrl;
    @Autowired
    private AuthUtil authUtil;

    @PostMapping("/open")
    public ResponseEntity<ChatConversation> openConversation(
            @RequestParam("otherId") Long otherId,
            @RequestParam(value = "listingId", required = false) Long listingId) {
        Long me = authUtil.getCurrentAccountIdOrNull();
        ChatConversation conv = chatService.openOrGetConversation(me, otherId, listingId);
        return ResponseEntity.ok(conv);
    }

    @GetMapping("/conversations")
    public ResponseEntity<Page<ChatConversation>> myConversations(Pageable pageable) {
        Long me = authUtil.getCurrentAccountIdOrNull();
        return ResponseEntity.ok(chatService.listConversations(me, pageable));
    }

    @GetMapping("/{cid}/messages")
    public ResponseEntity<Page<ChatMessageDto>> listMessages(@PathVariable("cid") Long cid, Pageable pageable) {
        Long me = authUtil.getCurrentAccountIdOrNull();
        return ResponseEntity.ok(chatService.listMessages(cid, me, pageable));
    }

    @PostMapping("/{cid}/text")
    public ResponseEntity<ChatMessageDto> sendText(@PathVariable("cid") Long cid,
                                                   @RequestParam("recipientId") Long recipientId,
                                                   @RequestBody SendTextRequest body) {
        Long me = authUtil.getCurrentAccountIdOrNull();
        ChatMessageDto dto = chatService.sendMessage(cid, me, recipientId, ChatMessageType.TEXT, body.text());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
    public record SendTextRequest(String text) {}

    @PostMapping(value = "/{cid}/media", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ChatMessageDto> sendMedia(@PathVariable("cid") Long cid,
                                                    @RequestParam("recipientId") Long recipientId,
                                                    @RequestParam("type") ChatMessageType type,
                                                    @RequestPart("file") MultipartFile file) {
        Long me = authUtil.getCurrentAccountIdOrNull();
        ChatMessageDto dto = chatService.sendMedia(cid, me, recipientId, type, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PostMapping("/{cid}/seen")
    public ResponseEntity<Integer> markSeen(@PathVariable("cid") Long cid) {
        Long me = authUtil.getCurrentAccountIdOrNull();
        return ResponseEntity.ok(chatService.markSeen(cid, me));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> unreadCount() {
        Long me = authUtil.getCurrentAccountIdOrNull();
        return ResponseEntity.ok(chatService.unreadCount(me));
    }

}
