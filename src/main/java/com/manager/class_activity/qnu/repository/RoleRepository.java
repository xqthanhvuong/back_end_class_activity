package com.manager.class_activity.qnu.repository;

import com.manager.class_activity.qnu.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByIdAndIsDeleted(Integer id, boolean deleted);

    @Query("Select r from Role r where r.isDeleted = false " +
            "and (:keyword is NULL or (lower(r.name) like lower(concat('%', :keyword, '%'))))")
    Page<Role> getRolesByPaged(Pageable pageable, String keyword);
}
