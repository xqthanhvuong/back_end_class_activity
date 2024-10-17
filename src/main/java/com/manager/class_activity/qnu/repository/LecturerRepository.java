package com.manager.class_activity.qnu.repository;

import com.manager.class_activity.qnu.entity.Lecturer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LecturerRepository extends JpaRepository<Lecturer, Integer> {
    Optional<Lecturer> findByIdAndIsDeleted(int id, boolean isDeleted);

    @Query("SELECT dp from Lecturer dp where dp.isDeleted = false " +
            "and (:keyword is NULL or (lower(dp.name) like lower(concat('%', :keyword, '%'))) " +
            "or (str(dp.id) like lower(concat('%', :keyword, '%'))) " +
            "or (dp.account.username like lower(concat('%', :keyword, '%'))))")
    Page<Lecturer> getLecturersByPaged(Pageable pageable, String keyword);

    boolean existsByEmailAndIsDeleted(String email, boolean isDeleted);
}
