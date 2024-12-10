package com.manager.class_activity.qnu.service;

import com.manager.class_activity.qnu.dto.response.PermissionResponse;
import com.manager.class_activity.qnu.entity.Permission;
import com.manager.class_activity.qnu.entity.Role;
import com.manager.class_activity.qnu.entity.Type;
import com.manager.class_activity.qnu.exception.BadException;
import com.manager.class_activity.qnu.exception.ErrorCode;
import com.manager.class_activity.qnu.repository.AccountRoleRepository;
import com.manager.class_activity.qnu.repository.PermissionRepository;
import com.manager.class_activity.qnu.repository.RolePermissionRepository;
import com.manager.class_activity.qnu.repository.TypeRepository;
import com.manager.class_activity.qnu.until.SecurityUtils;
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
    TypeRepository typeRepository;

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

    public Set<PermissionResponse> getPermissionOfType() {
        Set<PermissionResponse> result = new HashSet<>();
        Type type = typeRepository.findByName(SecurityUtils.getCurrentUserType()).orElseThrow(
                ()-> new BadException(ErrorCode.TYPE_ERROR)
        );
        List<Permission> permissions = permissionRepository.findByType(type);
        System.out.println(SecurityUtils.getCurrentUserType());
        for (Permission item: permissions) {
            result.add(PermissionResponse.builder()
                            .id(item.getId())
                            .name(item.getName())
                    .build());
        }
        return result;
    }
}
