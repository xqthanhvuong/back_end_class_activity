package com.manager.class_activity.qnu.repository;

import com.manager.class_activity.qnu.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByUsernameAndIsDeleted(String username, boolean deleted);
}