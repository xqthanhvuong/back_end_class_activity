package com.manager.class_activity.qnu.repository;

import com.manager.class_activity.qnu.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {

    @Query("SELECT dp from Department dp where dp.isDeleted = false " +
            "and (:keyword is NULL or lower(dp.name) like lower(concat('%', :keyword, '%')) " +
            "or str(dp.id) like lower(concat('%', :keyword, '%')))")
    Page<Department> getDepartmentsByPaged(Pageable pageable, String keyword);

    Optional<Department> findByIdAndIsDeleted(Integer id, boolean isDeleted);

}
