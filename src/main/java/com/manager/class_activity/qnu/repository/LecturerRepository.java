package com.manager.class_activity.qnu.repository;

import com.manager.class_activity.qnu.entity.Lecturer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface LecturerRepository extends JpaRepository<Lecturer, Integer> {
    Optional<Lecturer> findByIdAndIsDeleted(int id, boolean isDeleted);

    @Query("SELECT dp from Lecturer dp where dp.isDeleted = false " +
            "and (:departmentId is null or dp.department.id = :departmentId)" +
            "and (:keyword is NULL or (lower(dp.name) like lower(concat('%', :keyword, '%'))) " +
            "or (str(dp.id) like lower(concat('%', :keyword, '%'))) " +
            "or (dp.account.username like lower(concat('%', :keyword, '%'))))")
    Page<Lecturer> getLecturersByPaged(Pageable pageable, String keyword, Integer departmentId);

    boolean existsByEmailAndIsDeleted(String email, boolean isDeleted);

    List<Lecturer> findAllByIsDeleted(boolean isDeleted);

    List<Lecturer> findAllByDepartment_IdAndIsDeleted(Integer departmentId, boolean isDeleted);

    @Modifying
    @Transactional
    @Query(value = """
    UPDATE lecturer l
    JOIN department d ON d.id = l.department_id
    SET l.is_deleted = 1
    WHERE d.id = :departmentId
""", nativeQuery = true)
    void deleteByDepartmentId(@Param("departmentId") int departmentId);

}
