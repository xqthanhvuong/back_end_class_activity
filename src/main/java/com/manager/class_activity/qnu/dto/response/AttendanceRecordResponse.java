package com.manager.class_activity.qnu.dto.response;

import com.manager.class_activity.qnu.entity.AttendanceStatusEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class AttendanceRecordResponse {
    int id;
    String studentCode;
    String studentName;
    Timestamp checkInTime;
    AttendanceStatusEnum status;
    Timestamp createdAt;
    Timestamp updatedAt;
}
