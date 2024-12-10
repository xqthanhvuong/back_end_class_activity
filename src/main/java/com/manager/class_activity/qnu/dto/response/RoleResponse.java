package com.manager.class_activity.qnu.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleResponse {
    String name;
    int id;
    int countHave;
    String Type;
    Timestamp createTime;
    Timestamp updateTime;
}
