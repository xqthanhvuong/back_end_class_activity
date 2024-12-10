package com.manager.class_activity.qnu.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class RoleCreationRequest {
    private String roleName;          // Tên của Role
    private List<Integer> permissionIds; // Danh sách ID của các Permission
}
