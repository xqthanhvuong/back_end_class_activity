package com.manager.class_activity.qnu.helper;

public class StringHelper {
    public static String processString(String input) {
        if (input == null) {
            return null; // Trả về null nếu đầu vào là null
        }

        // Loại bỏ khoảng trắng ở đầu và cuối chuỗi
        input = input.trim();

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

}
