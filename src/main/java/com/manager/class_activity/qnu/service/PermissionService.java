package com.manager.class_activity.qnu.service;

import com.manager.class_activity.qnu.entity.Permission;
import com.manager.class_activity.qnu.entity.Role;
import com.manager.class_activity.qnu.repository.AccountRoleRepository;
import com.manager.class_activity.qnu.repository.PermissionRepository;
import com.manager.class_activity.qnu.repository.RolePermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class PermissionService {
    PermissionRepository permissionRepository;
    RolePermissionRepository rolePermissionRepository;
    AccountRoleRepository accountRoleRepository;

    Set<String> getPermissionNamesOfAccount(String userName){
        Set<String> permissions = new HashSet<>();
        List<Role> rolesOfAccount = accountRoleRepository.findRoleByUserName(userName, false);
        for(Role role : rolesOfAccount){
            permissions.addAll(getPermissionNamesOfRole(role.getId()));
        }
        return permissions;
    }

    Set<String> getPermissionNamesOfRole(int roleId){
        Set<String> result = new HashSet<>();
        List<Permission> permissions = rolePermissionRepository.findPermissionsByRoleId(roleId, false);
        for (Permission item : permissions) {
            result.add(item.getName());
        }
        return result;
    }

}
