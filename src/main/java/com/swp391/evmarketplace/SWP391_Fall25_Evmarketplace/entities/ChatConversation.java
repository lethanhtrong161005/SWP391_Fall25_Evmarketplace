package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "chat_conversation",
        indexes = {
                @Index(name = "idx_chatconv_listing", columnList = "listing_id"),
                @Index(name = "idx_chatconv_userA", columnList = "user_a_id,last_message_at"),
                @Index(name = "idx_chatconv_userB", columnList = "user_b_id,last_message_at"),
                @Index(name = "idx_chatconv_last",  columnList = "last_message_at")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_chat_pair", columnNames = {"user_min_id","user_max_id"})
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatConversation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_a_id", nullable = false)
    private Long userAId;

    @Column(name = "user_b_id", nullable = false)
    private Long userBId;

    @Column(name = "listing_id")
    private Long listingId;

    @Column(name = "user_min_id", nullable = false)
    private Long userMinId;

    @Column(name = "user_max_id", nullable = false)
    private Long userMaxId;

    @Column(name = "last_message_id")
    private Long lastMessageId;

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
