package com.manager.class_activity.qnu.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ActivityResponse {
    int id;
    String name;
    Timestamp updatedAt;
    Timestamp createdAt;
}
