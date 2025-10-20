package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Page<ChatMessage> findByConversationIdOrderByCreatedAtDesc(Long conversationId, Pageable pageable);

    long countByRecipientIdAndSeenAtIsNull(Long recipientId);

    @Modifying
    @Query("""
           update ChatMessage m
           set m.seenAt = :ts
           where m.conversationId = :cid and m.recipientId = :uid and m.seenAt is null
           """)
    int markSeenInConversation(@Param("cid") Long conversationId,
                               @Param("uid") Long recipientId,
                               @Param("ts") LocalDateTime ts);

}
