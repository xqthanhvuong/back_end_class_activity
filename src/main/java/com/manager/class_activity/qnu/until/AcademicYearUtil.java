package com.manager.class_activity.qnu.until;

import java.time.LocalDate;
import java.time.Month;

public class AcademicYearUtil {
    public static String getCurrentAcademicYear() {
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();

        // Check if the date is before or after 1st September
        if (today.isBefore(LocalDate.of(currentYear, Month.SEPTEMBER, 1))) {
            return (currentYear - 1) + "-" + currentYear;
        } else {
            return currentYear + "-" + (currentYear + 1);
        }
    }
}
