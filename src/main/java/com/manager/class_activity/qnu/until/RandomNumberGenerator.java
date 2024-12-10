package com.manager.class_activity.qnu.until;

import java.util.Random;

public class RandomNumberGenerator {
    public static String generateRandomSixDigit() {
        Random random = new Random();
        int number = random.nextInt(1_000_000); // 0 đến 999999
        return String.format("%06d", number);
    }

}
