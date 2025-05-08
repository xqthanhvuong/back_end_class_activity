package com.manager.class_activity.qnu.until;

public class StringUtils {
    public static String removeAllSpaces(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("\\s+", "");
    }
}
