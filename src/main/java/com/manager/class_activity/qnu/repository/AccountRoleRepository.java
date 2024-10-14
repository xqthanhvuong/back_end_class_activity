package com.manager.class_activity.qnu.repository;

import com.manager.class_activity.qnu.entity.AccountRole;
import com.manager.class_activity.qnu.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRoleRepository extends JpaRepository<AccountRole, Integer> {
    @Query("SELECT ar.role from AccountRole ar where ar.account.username = :userName and ar.role.isDeleted =:isDeleted")
    List<Role> findRoleByUserName(@Param("userName") String userName, @Param("isDeleted") Boolean isDeleted);
}
