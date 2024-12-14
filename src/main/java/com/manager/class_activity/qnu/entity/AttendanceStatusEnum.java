package com.manager.class_activity.qnu.entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public enum AttendanceStatusEnum {
    Present,
    Absent,
    Excused,
    Late;

    public static AttendanceStatusEnum getStatusBasedOnTime(Timestamp activityTime) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime activityDateTime = activityTime.toLocalDateTime();
        long minutesDifference = ChronoUnit.MINUTES.between(activityDateTime, now);
        if (minutesDifference > 15) {
            return AttendanceStatusEnum.Late; // Muộn hơn 15 phút
        }  else {
            return AttendanceStatusEnum.Present; // Có mặt đúng giờ
        }
    }
}
