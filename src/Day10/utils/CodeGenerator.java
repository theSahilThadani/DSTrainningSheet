package Day10.utils;

import java.util.Random;

public class CodeGenerator {
    private static final String CHARSET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_-";
    private static final int DEFAULT_LENGTH = 6;
    private static final Random random = new Random();
    private static long sequenceCounter = 1000000;

    //this will generate random short code in O(N)
    public static String generateRandomCode(int length) {
        if (length < 4 || length > 20) {
            length = DEFAULT_LENGTH;
        }

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARSET.charAt(random.nextInt(CHARSET.length())));
        }
        return sb.toString();
    }

    //this will generate short code using base62 encoded from number in O(Log n)
    public static String generateBase62Code() {
        synchronized (CodeGenerator.class) {
            sequenceCounter++;
            return toBase62(sequenceCounter);
        }
    }


     // Convert number to base62 string in O(log n)
    private static String toBase62(long num) {
        if (num == 0) return "0";

        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            sb.append(CHARSET.charAt((int)(num % 62)));
            num /= 62;
        }
        return sb.reverse().toString();
    }

    //this used to generate random num and base62 string from timestamp
    public static String generateHybridCode() {
        long timestamp = System.currentTimeMillis();
        int randomPart = random.nextInt(10000);
        String base = toBase62(timestamp).substring(0, Math.min(3, toBase62(timestamp).length()));
        String randomStr = toBase62(randomPart);
        return base + randomStr;
    }

}