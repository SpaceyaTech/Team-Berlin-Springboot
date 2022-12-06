package com.spaceyatech.berlin.repository;

import com.spaceyatech.berlin.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {


    @Query(value = "SELECT * FROM account where users_id = :userId",nativeQuery = true)
    public List<Account> findByUserId(UUID userId);

}
