package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ChatConversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatConversationRepository extends JpaRepository<ChatConversation, Long> {
    Optional<ChatConversation> findByUserMinIdAndUserMaxId(Long userMinId, Long userMaxId);

    @Query("""
           select (count(c) > 0)
           from ChatConversation c
           where c.id = :cid and (c.userAId = :uid or c.userBId = :uid)
           """)
    boolean isMember(@Param("cid") Long conversationId, @Param("uid") Long uid);

    @Query("""
           select c from ChatConversation c
           where (c.userAId = :uid or c.userBId = :uid)
           order by case when c.lastMessageAt is null then 1 else 0 end,
                    c.lastMessageAt desc, c.updatedAt desc
           """)
    Page<ChatConversation> findConversationsOf(@Param("uid") Long uid, Pageable pageable);
}
