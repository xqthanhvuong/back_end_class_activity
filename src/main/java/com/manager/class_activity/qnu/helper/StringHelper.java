package com.manager.class_activity.qnu.helper;

import java.util.Calendar;
import java.util.Date;

public class StringHelper {
    public static String processString(String input) {
        if (input == null) {
            return null; // Trả về null nếu đầu vào là null
        }

        // Loại bỏ khoảng trắng ở đầu và cuối chuỗi
        input = input.trim().toLowerCase();

        // Nếu chuỗi rỗng sau khi loại bỏ khoảng trắng, trả về chuỗi rỗng
        if (input.isEmpty()) {
            return "";
        }

        // Viết hoa chữ cái đầu tiên
        String[] words = input.split(" ");
        StringBuilder processedString = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                // Viết hoa chữ cái đầu và thêm từ vào StringBuilder
                processedString.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1)).append(" ");
            }
        }

        // Trả về chuỗi đã xử lý, loại bỏ khoảng trắng ở cuối
        return processedString.toString().trim();
    }
    public static String createPassword(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        String day = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)); // Ngày 2 chữ số
        String month = String.format("%02d", calendar.get(Calendar.MONTH) + 1); // Tháng 2 chữ số
        String year = String.format("%02d", calendar.get(Calendar.YEAR) % 100); // Lấy 2 chữ số cuối của năm

        return day + month + year;
    }


}
