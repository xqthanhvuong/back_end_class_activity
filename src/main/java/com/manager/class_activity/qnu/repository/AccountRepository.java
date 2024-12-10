package com.manager.class_activity.qnu.repository;

import com.manager.class_activity.qnu.entity.Account;
import com.manager.class_activity.qnu.entity.Lecturer;
import com.manager.class_activity.qnu.entity.Staff;
import com.manager.class_activity.qnu.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByUsernameAndIsDeleted(String username, boolean deleted);

    Optional<Account> findByIdAndIsDeleted(int id, boolean b);

    boolean existsByUsernameAndIsDeleted(String username, boolean deleted);

    @Query("select l from Lecturer l where l.isDeleted = false and l.account.username = :username ")
    Lecturer getLecturerByUsername(String username);

    @Query("select l from Staff l where l.isDeleted = false and l.account.username = :username ")
    Staff getStaffByUsername(String username);

    @Query("select l from Student l where l.isDeleted = false and l.account.username = :username ")
    Student getStudentByUsername(String username);

}