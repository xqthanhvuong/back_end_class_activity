package com.manager.class_activity.qnu.service;

import com.manager.class_activity.qnu.dto.request.Filter;
import com.manager.class_activity.qnu.dto.response.*;
import com.manager.class_activity.qnu.entity.*;
import com.manager.class_activity.qnu.exception.BadException;
import com.manager.class_activity.qnu.exception.ErrorCode;
import com.manager.class_activity.qnu.helper.CustomPageRequest;
import com.manager.class_activity.qnu.mapper.RoleMapper;
import com.manager.class_activity.qnu.repository.*;
import com.manager.class_activity.qnu.until.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    List<Integer> rolesDefault = Arrays.asList(1, 2, 3);
    AccountService accountService;
    AccountRoleRepository accountRoleRepository;
    RolePermissionRepository rolePermissionRepository;
    RoleMapper roleMapper;
    TypeRepository typeRepository;

    @Transactional
    public void createRoleWithPermissions(String roleName, List<Integer> permissionIds) {
        // Lấy danh sách Permissions từ database
        List<Permission> permissions = permissionRepository.findAllById(permissionIds);
        if (permissions.isEmpty()) {
            throw new BadException(ErrorCode.PERMISSION_NOT_FOUND);
        }
        Type type = typeRepository.findByName(SecurityUtils.getCurrentUserType()).orElseThrow(
                ()-> new BadException(ErrorCode.PERMISSION_NOT_FOUND)
        );

        // Tạo Role mới
        Role newRole = new Role();
        newRole.setName(roleName);
        newRole.setType(type);

        // Tạo RolePermission
        List<RolePermission> rolePermissions = permissions.stream()
                .map(permission -> new RolePermission(null, newRole, permission, null))
                .toList();

        // Lưu các RolePermission
        newRole.setRolePermissions(Set.copyOf(rolePermissions));
        roleRepository.save(newRole);
    }

    public void updateRoleWithPermissions(int roleId, String roleName, List<Integer> permissionIds) {
        Role role = roleRepository.findByIdAndIsDeleted(roleId, false)
                .orElseThrow(() -> new BadException(ErrorCode.ROLE_NOT_FOND));
        role.setName(roleName);

        List<Permission> permissions = permissionRepository.findAllById(permissionIds);

        if (permissions.size() != permissionIds.size()) {
            throw new BadException(ErrorCode.PERMISSION_NOT_FOUND);
        }
        Set<RolePermission> existingRolePermissions = role.getRolePermissions();
        Set<Permission> permissionSet = new HashSet<>(permissions);
        Set<RolePermission> permissionsToRemove = new HashSet<>();
        for (RolePermission rolePermission : existingRolePermissions) {
            if (!permissionSet.contains(rolePermission.getPermission())) {
                permissionsToRemove.add(rolePermission);
            }
        }
        rolePermissionRepository.deleteAll(permissionsToRemove);

        Set<RolePermission> updatedRolePermissions = new HashSet<>(existingRolePermissions);
        for (Permission permission : permissions) {
            boolean alreadyHasPermission = existingRolePermissions.stream()
                    .anyMatch(rp -> rp.getPermission().equals(permission));

            if (!alreadyHasPermission) {
                updatedRolePermissions.add(new RolePermission(null, role, permission, null));
            }
        }
        role.setRolePermissions(updatedRolePermissions);
        roleRepository.save(role);
    }


    public void deleteRole(int roleId) {
        Role role = roleRepository.findByIdAndIsDeleted(roleId, false).orElseThrow(() ->
                new BadException(ErrorCode.ROLE_NOT_FOND)
        );
        if (rolesDefault.contains(role.getId())) {
            throw new BadException(ErrorCode.CANT_DELETE);
        }
        role.setDeleted(true);
        roleRepository.save(role);
    }

    public void addRoleForAccount(String accountName, Integer roleId) {
        Role role = roleRepository.findByIdAndIsDeleted(roleId, false).orElseThrow(
                () -> new BadException(ErrorCode.ROLE_NOT_FOND)
        );
        Account account = accountService.getAccount(accountName);
        AccountRole accountRoleCheck = accountRoleRepository.findByAccountAndRole(account, role);
        if (ObjectUtils.isNotEmpty(accountRoleCheck)) {
            return;
        }
        if (!account.getType().equals(role.getType())) {
            throw new BadException(ErrorCode.TYPE_NOT_MATCH);
        }

        AccountRole accountRole = AccountRole.builder()
                .account(account)
                .role(role)
                .build();
        accountRoleRepository.save(accountRole);
    }

    public RoleDetailResponse getAllPermissionOfRole(int roleId) {
        Role role = roleRepository.findByIdAndIsDeleted(roleId, false).orElseThrow(
                () -> new BadException(ErrorCode.ROLE_NOT_FOND)
        );
        Type type = typeRepository.findByName(SecurityUtils.getCurrentUserType()).orElseThrow(
                () -> new BadException(ErrorCode.ROLE_NOT_FOND)
        );
        if (!type.equals(role.getType())) {
            throw new BadException(ErrorCode.PERMISSION_NOT_FOUND);
        }

        List<Integer> rs = new ArrayList<>();
        List<Permission> permissions = rolePermissionRepository.findPermissionsByRoleId(role.getId(), false);
        for (Permission item : permissions) {
            rs.add(item.getId());
        }
        return RoleDetailResponse.builder().roleName(role.getName()).permissionIds(rs).build();
    }

    @Transactional
    public Set<AccountHaveRole> getAllAccountHaveRole(int roleId) {
        Role role = roleRepository.findByIdAndIsDeleted(roleId, false).orElseThrow(
                () -> new BadException(ErrorCode.ROLE_NOT_FOND)
        );
        Set<AccountHaveRole> result = new HashSet<>();
        for (AccountRole item : role.getAccountRoles()) {
            result.add(AccountHaveRole.builder()
                            .username(item.getAccount().getUsername())
                            .type(item.getRole().getType().getName())
                            .id(item.getId())
                            .name(accountService.getNameOfAccount(item.getAccount().getUsername()))
                    .build());
        }
        return result;

    }

    public Set<AccountHaveRole> getAllAccountHaveRole() {
        List<AccountRole> accountRoles = accountRoleRepository.findAll();
        Set<AccountHaveRole> result = new HashSet<>();
        for (AccountRole item : accountRoles) {
            result.add(AccountHaveRole.builder()
                    .username(item.getAccount().getUsername())
                    .type(item.getRole().getType().getName())
                    .id(item.getId())
                    .name(accountService.getNameOfAccount(item.getAccount().getUsername()))
                    .build());
        }
        return result;
    }


    public void deleteAccountRole(int id) {
        AccountRole accountRole = accountRoleRepository.findById(id).orElseThrow(
                () -> new BadException(ErrorCode.ROLE_NOT_FOND)
        );
        accountRoleRepository.delete(accountRole);
    }

    public PagedResponse<RoleResponse> getRoles(CustomPageRequest<Filter> request) {
        Page<Role> roles = roleRepository.getRolesByPaged(request.toPageable(), request.getKeyWord());
        List<RoleResponse> roleResponses = new ArrayList<>();
        if (!"SUPERADMIN".equals(SecurityUtils.getCurrentUserType())) {
            for (Role role : roles.getContent()) {
                if (role.getType().getName().equals(SecurityUtils.getCurrentUserType())) {
                    roleResponses.add(RoleResponse.builder()
                            .id(role.getId())
                            .Type(role.getType().getName())
                            .countHave(role.getAccountRoles().size())
                            .createTime(role.getCreatedAt())
                            .updateTime(role.getUpdatedAt())
                            .name(role.getName())
                            .build());
                }
            }
        } else {
            for (Role role : roles.getContent()) {
                roleResponses.add(RoleResponse.builder()
                        .id(role.getId())
                        .Type(role.getType().getName())
                        .countHave(role.getAccountRoles().size())
                        .createTime(role.getCreatedAt())
                        .updateTime(role.getUpdatedAt())
                        .name(role.getName())
                        .build());
            }
        }
        return new PagedResponse<>(
                roleResponses,
                roles.getNumber(),
                roles.getTotalElements(),
                roles.getTotalPages(),
                roles.isLast()
        );
    }
}
