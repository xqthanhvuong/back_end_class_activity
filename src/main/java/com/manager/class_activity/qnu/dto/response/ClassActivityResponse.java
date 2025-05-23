package com.manager.class_activity.qnu.dto.response;

import com.manager.class_activity.qnu.entity.Status;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassActivityResponse {
    int id;
    int activityId;
    String className;
    int classId;
    String leader;
    Timestamp activityTime;
    String activityView;
    Status status;
    Timestamp createdAt;
    Timestamp updatedAt;
}
