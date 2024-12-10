package com.manager.class_activity.qnu.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountRoleRequest {
    String accountName;
    int roleId;
}
