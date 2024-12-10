package com.manager.class_activity.qnu.repository;

import com.manager.class_activity.qnu.entity.Permission;
import com.manager.class_activity.qnu.entity.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer> {
    List<Permission> findByType(Type type);
}
