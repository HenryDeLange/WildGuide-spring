package mywild.wildguide.domain.file.logic;

import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Component;

@Component
public class FileIdGenerator {

    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public String generateId() {
        long millis = System.currentTimeMillis() - 1700000000000L; // Subtract a fixed epoch to shorten the number
        String timestamp = encodeBase62(millis);
        int rand = ThreadLocalRandom.current().nextInt(62 * 62); // Add 2 random base62 chars
        String random = encodeBase62(rand);
        return timestamp + random;
    }

    private String encodeBase62(long value) {
        StringBuilder stringBuilder = new StringBuilder();
        while (value > 0) {
            int remainder = (int) (value % 62);
            stringBuilder.append(BASE62.charAt(remainder));
            value /= 62;
        }
        return stringBuilder.reverse().toString();
    }

}
