package com.manager.class_activity.qnu.until;

import java.time.*;
import java.time.temporal.TemporalAdjusters;

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
}
