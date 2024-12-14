package com.manager.class_activity.qnu.dto.request;

import com.manager.class_activity.qnu.entity.AttendanceStatusEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class AttendanceRecordRequest {
    AttendanceStatusEnum attendanceStatus;
    int id;
}
