package Day6_7.entities;

import java.util.Objects;

public class Message {

    private final String messageId;
    private final String senderId;
    private final String conversationId;
    private final String content;
    private final long timestamp;
    private final MessageType type;


    private MessageStatus status;
    private boolean isDeleted;
    private long deliveredAt;
    private long readAt;

    //constraint for message/content
    private static final int MAX_CONTENT_LENGTH = 2000;
    private static final int MIN_CONTENT_LENGTH = 1;


    public Message(String messageId, String senderId, String conversationId,
                   String content, MessageType type) {

        // Validation
        validateId(messageId, "Message ID");
        validateId(senderId, "Sender ID");
        validateId(conversationId, "Conversation ID");
        validateContent(content);

        if (type == null) {
            throw new IllegalArgumentException("Message type cannot be null");
        }


        this.messageId = messageId;
        this.senderId = senderId;
        this.conversationId = conversationId;
        this.content = content.trim();
        this.timestamp = System.currentTimeMillis();
        this.type = type;


        this.status = MessageStatus.PENDING;
        this.isDeleted = false;
        this.deliveredAt = 0;
        this.readAt = 0;
    }


    private void validateId(String id, String fieldName) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
    }


     // Validate content length
    private void validateContent(String content) {
        if (content == null) {
            throw new IllegalArgumentException("Content cannot be null");
        }

        String trimmed = content.trim();

        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Content cannot be empty");
        }

        if (trimmed.length() > MAX_CONTENT_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Content exceeds max length (%d characters)", MAX_CONTENT_LENGTH)
            );
        }
    }

    // Getters
    public String getMessageId() { return messageId; }
    public String getSenderId() { return senderId; }
    public String getConversationId() { return conversationId; }
    public String getContent() { return content; }
    public long getTimestamp() { return timestamp; }
    public MessageType getType() { return type; }
    public MessageStatus getStatus() { return status; }
    public boolean isDeleted() { return isDeleted; }
    public long getDeliveredAt() { return deliveredAt; }
    public long getReadAt() { return readAt; }

    //update message status.
    public synchronized void updateStatus(MessageStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }

        // Validate state transition
        if (!isValidStatusTransition(this.status, newStatus)) {
            throw new IllegalStateException(
                    String.format("Invalid status transition: %s → %s",
                            this.status, newStatus)
            );
        }

        this.status = newStatus;

        // Track timestamps
        if (newStatus == MessageStatus.DELIVERED) {
            this.deliveredAt = System.currentTimeMillis();
        } else if (newStatus == MessageStatus.READ) {
            this.readAt = System.currentTimeMillis();
        }
    }

    //validates state of status pending -> sent => delivered -> read or failed if issue.
    private boolean isValidStatusTransition(MessageStatus from, MessageStatus to) {
        if (to == MessageStatus.FAILED) {
            return true;  // Can fail from any state
        }

        switch (from) {
            case PENDING:
                return to == MessageStatus.SENT;
            case SENT:
                return to == MessageStatus.DELIVERED;
            case DELIVERED:
                return to == MessageStatus.READ;
            case READ:
            case FAILED:
                return false;
            default:
                return false;
        }
    }

    //mark as delete make soft delete.
    public synchronized void markAsDeleted() {
        this.isDeleted = true;
    }


    //check if message can undo, if only not deleted already, not read, sent within 5 mins.
    public boolean canUndo() {
        if (isDeleted) return false;
        if (status == MessageStatus.READ) return false;

        long now = System.currentTimeMillis();
        long fiveMinutes = 5 * 60 * 1000;

        return (now - timestamp) < fiveMinutes;
    }

    //time of message
    public long getAge() {
        return System.currentTimeMillis() - timestamp;
    }

    //formatter
    public String getFormattedTimestamp() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss");
        return sdf.format(new java.util.Date(timestamp));
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId);
    }

    @Override
    public String toString() {
        String contentPreview = content.length() > 50
                ? content.substring(0, 47) + "..."
                : content;

        return String.format("Message{id='%s', sender='%s', status=%s, content='%s'}",
                messageId.substring(0, 8), senderId, status, contentPreview);
    }

    //for debugging
    public String toDetailedString() {
        return String.format(
                "Message{\n" +
                        "  id: %s\n" +
                        "  sender: %s\n" +
                        "  conversation: %s\n" +
                        "  content: %s\n" +
                        "  timestamp: %s\n" +
                        "  status: %s\n" +
                        "  type: %s\n" +
                        "  deleted: %s\n" +
                        "}",
                messageId, senderId, conversationId, content,
                getFormattedTimestamp(), status, type, isDeleted
        );
    }
}
