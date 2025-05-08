package com.manager.class_activity.qnu.controller;

import com.manager.class_activity.qnu.dto.request.AccountRoleRequest;
import com.manager.class_activity.qnu.dto.request.Filter;
import com.manager.class_activity.qnu.dto.request.RoleCreationRequest;
import com.manager.class_activity.qnu.dto.response.*;
import com.manager.class_activity.qnu.helper.CustomPageRequest;
import com.manager.class_activity.qnu.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/roles")
public class RoleController {
    RoleService roleService;

    @PostMapping()
    public JsonResponse<String> addRole(@RequestBody RoleCreationRequest role) {
        roleService.createRoleWithPermissions(role.getRoleName(), role.getPermissionIds());
        return JsonResponse.success("Add role success");
    }

    @GetMapping("/{id}")
    public JsonResponse<RoleDetailResponse> getRoles(@PathVariable int id) {
        return JsonResponse.success(roleService.getAllPermissionOfRole(id));
    }

    @GetMapping("/accounts/{id}")
    public JsonResponse<Set<AccountHaveRole>> getAccountRoles(@PathVariable int id) {
        return JsonResponse.success(roleService.getAllAccountHaveRole(id));
    }

    @GetMapping("/accounts")
    public JsonResponse<Set<AccountHaveRole>> getAccountRoles() {
        return JsonResponse.success(roleService.getAllAccountHaveRole());
    }

    @PutMapping("/{id}")
    public JsonResponse<String> updateRole(@PathVariable int id,@RequestBody RoleCreationRequest role) {
        roleService.updateRoleWithPermissions(id ,role.getRoleName(), role.getPermissionIds());
        return JsonResponse.success("update role success");
    }

    @DeleteMapping("/accounts/account-role/{id}")
    public JsonResponse<String> deleteAccountRole(@PathVariable int id) {
        roleService.deleteAccountRole(id);
        return JsonResponse.success("Delete account role success");
    }

    @DeleteMapping("/{id}")
    public JsonResponse<String> deleteRole(@PathVariable int id) {
        roleService.deleteRole(id);
        return JsonResponse.success("Delete role success");
    }

    @PostMapping("/get-roles")
    public JsonResponse<PagedResponse<RoleResponse>> searchRoles(@RequestBody CustomPageRequest<Filter> request) {
        PagedResponse<RoleResponse> response = roleService.getRoles(request);
        return JsonResponse.success(response);
    }

    @PostMapping("/accounts")
    public JsonResponse<String> addAccountRole(@RequestBody AccountRoleRequest request){
        roleService.addRoleForAccount(request.getAccountName(), request.getRoleId());
        return JsonResponse.success("Add account role success");
    }


}
