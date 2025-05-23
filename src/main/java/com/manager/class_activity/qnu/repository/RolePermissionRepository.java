package com.manager.class_activity.qnu.repository;

import com.manager.class_activity.qnu.entity.Permission;
import com.manager.class_activity.qnu.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Integer> {
    @Query("SELECT rp.permission FROM RolePermission rp WHERE rp.role.id = :roleId and rp.permission.isDeleted = :isDeleted")
    List<Permission> findPermissionsByRoleId(@Param("roleId")Integer roleId, @Param("isDeleted") Boolean isDeleted);
    
    Set<RolePermission> findByRole_Id(Integer roleId);

    @Modifying
    @Query("delete from RolePermission r where r.id = :roleId")
    void deleteByRole_Id(Integer roleId);
}
