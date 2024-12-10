package com.manager.class_activity.qnu.controller;

import com.manager.class_activity.qnu.dto.response.JsonResponse;
import com.manager.class_activity.qnu.dto.response.PermissionResponse;
import com.manager.class_activity.qnu.service.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/permissions")
public class PermissionController {
    PermissionService permissionService;

    @GetMapping()
    public JsonResponse<Set<PermissionResponse>> getPermissions() {
        return JsonResponse.success(permissionService.getPermissionOfType());
    }
}
