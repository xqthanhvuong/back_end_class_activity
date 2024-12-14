package com.manager.class_activity.qnu.until;

import java.sql.Timestamp;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.ChronoUnit;

public class DateTimeUtils {
    public static LocalDateTime getLastFridayOfCurrentMonthAt10AM() {
        // Lấy múi giờ mặc định của máy tính
        ZoneId zoneId = ZoneId.systemDefault();

        // Lấy thời gian hiện tại
        LocalDateTime now = LocalDateTime.now(zoneId);

        // Xác định ngày đầu tiên của tháng kế tiếp
        LocalDateTime firstDayOfNextMonth = now
                .with(TemporalAdjusters.firstDayOfNextMonth())
                .withHour(10) // Đặt giờ là 10
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        // Xác định ngày thứ 6 cuối cùng của tháng hiện tại
        return firstDayOfNextMonth
                .minusDays(1) // Lùi 1 ngày để quay về tháng hiện tại
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.FRIDAY));
    }
    public static boolean compareTimestamp(Timestamp activityTime) {
        // Lấy thời gian hiện tại
        LocalDateTime now = LocalDateTime.now();

        // Chuyển Timestamp thành LocalDateTime
        LocalDateTime activityDateTime = activityTime.toLocalDateTime();

        // Tính sự khác biệt về phút giữa hai thời gian
        long minutesDifference = ChronoUnit.MINUTES.between(now, activityDateTime);

        return minutesDifference <= 30;
    }

    public static boolean isSoLate(Timestamp end) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDateTime = end.toLocalDateTime();
        long minutesDifference = ChronoUnit.MINUTES.between(now, endDateTime);

        return minutesDifference >= 0;
    }
}
