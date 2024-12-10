package com.manager.class_activity.qnu.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuideResponse {
    int id;
    String name;
    String url;
    Timestamp createdAt;
    Timestamp updatedAt;
    boolean isDepartment;
}
