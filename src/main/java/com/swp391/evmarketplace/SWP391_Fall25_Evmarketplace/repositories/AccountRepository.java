package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long> {
    Optional<Account> findByPhoneNumber(String phoneNumber);
    Optional<Account> findByEmail(String email);
    Optional<Account> findByGoogleId(String googleId);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);

    @EntityGraph(attributePaths = {"profile"})
    Page<Account> findAllAccountBy(Pageable pageable);

    @EntityGraph(attributePaths = "profile")
    Page<Account> findByProfileFullNameContainingIgnoreCase(String keyword, Pageable pageable);
}
