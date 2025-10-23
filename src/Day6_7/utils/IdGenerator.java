package Day6_7.utils;
import java.util.UUID;

public class IdGenerator {

    public static String generateUserId() {

        return UUID.randomUUID().toString();
    }

    public static String generateMessageId() {
        return UUID.randomUUID().toString();
    }

    public static String generateConversationId() {
        return UUID.randomUUID().toString();
    }
}
