package com.manager.class_activity.qnu.helper;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimestampConverter {
    public static Timestamp convert(String timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(timestamp, formatter);
        return Timestamp.valueOf(localDateTime);
    }
}
