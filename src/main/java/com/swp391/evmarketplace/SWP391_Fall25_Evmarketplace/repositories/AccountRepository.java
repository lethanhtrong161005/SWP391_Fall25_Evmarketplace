package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Account;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.AccountRole;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.AccountStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /**
     * Dùng để chặn 2 request đồng thời mà phải thực hiện tuần tự
     **/
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.id = :id")
    Optional<Account> lockById(@Param("id") Long id);

    @EntityGraph(attributePaths = {"branch"})
    List<Account> findByRoleAndStatusAndBranch_Id(AccountRole role, AccountStatus status, Long branchId);
}
