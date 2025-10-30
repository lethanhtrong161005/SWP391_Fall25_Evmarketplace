package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories;


import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long>, JpaSpecificationExecutor<Contract> {
    boolean existsByOrder_Id(Long orderId);
    Optional<Contract> findByOrder_Id(Long orderId);
    Optional<Contract> findById(Long id);


}
