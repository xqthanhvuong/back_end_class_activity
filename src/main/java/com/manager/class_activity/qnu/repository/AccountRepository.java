package com.manager.class_activity.qnu.repository;

import com.manager.class_activity.qnu.entity.Account;
import com.manager.class_activity.qnu.entity.Lecturer;
import com.manager.class_activity.qnu.entity.Staff;
import com.manager.class_activity.qnu.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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


    @Modifying
    @Transactional
    @Query(value = """
        UPDATE `account`
        SET `is_deleted` = true
        WHERE `id` IN (
            SELECT `account_id`
            FROM `staff`
            WHERE `department_id` = :departmentId
        )
        OR `id` IN (
            SELECT `account_id`
            FROM `student`
            WHERE `class_id` IN (
                SELECT `id` FROM `class` WHERE `department_id` = :departmentId
            )
        )
        OR `id` IN (
            SELECT `account_id`
            FROM `lecturer`
            WHERE `department_id` = :departmentId
        );
        """, nativeQuery = true)
    void deleteAccountByDepartmentId(int departmentId);

    @Modifying
    @Transactional
    @Query(value = """
        UPDATE `account`
        SET `is_deleted` = true
        WHERE `id` IN (
            SELECT `account_id`
            FROM `student`
            WHERE `class_id` IN (
                SELECT `id` FROM `class` WHERE `course_id` = :courseId
            )
        );
        """, nativeQuery = true)
    void deleteAccountByCourseId(int courseId);

    @Modifying
    @Transactional
    @Query(value = """
        UPDATE `account`
        SET `is_deleted` = true
        WHERE `id` IN (
            SELECT `account_id`
            FROM `student`
            WHERE `class_id` = :classId 
        );
        """, nativeQuery = true)
    void deleteAccountByClassId(int classId);

}