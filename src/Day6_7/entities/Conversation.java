package Day6_7.entities;
import Day6_7.managers.MessageHistory;

import java.util.*;

public class Conversation {
    private final String conversationId;
    private final Set<String> participants;  // User IDs
    private final ConversationType type;
    private final long createdAt;

    // Message storage and processing
    private final MessageHistory history;
    private final Deque<Message> messageQueue;  // Pending messages
    private final Map<String, Deque<Message>> undoStacks;  // Per-user undo

    // Metadata
    private String lastMessageId;
    private long lastActivityAt;
    private final Map<String, Integer> unreadCount;  // Per user

    // Constants
    private static final int MAX_HISTORY = 100;
    private static final int MAX_UNDO_STACK = 50;

    /**
     * Create new conversation
     *
     * @param conversationId Unique ID
     * @param participants Set of user IDs (immutable copy)
     * @param type ONE_TO_ONE or GROUP
     */
    public Conversation(String conversationId, Set<String> participants, ConversationType type) {
        // Validation
        if (conversationId == null || conversationId.trim().isEmpty()) {
            throw new IllegalArgumentException("Conversation ID cannot be empty");
        }
        if (participants == null || participants.isEmpty()) {
            throw new IllegalArgumentException("Participants cannot be empty");
        }
        if (type == ConversationType.ONE_TO_ONE && participants.size() != 2) {
            throw new IllegalArgumentException("One-to-one conversation requires exactly 2 participants");
        }
        if (participants.size() < 2) {
            throw new IllegalArgumentException("Conversation requires at least 2 participants");
        }

        // Immutable fields
        this.conversationId = conversationId;
        this.participants = new HashSet<>(participants);  // Defensive copy
        this.type = type;
        this.createdAt = System.currentTimeMillis();

        // Initialize storage
        this.history = new MessageHistory(MAX_HISTORY);
        this.messageQueue = new ArrayDeque<>();
        this.undoStacks = new HashMap<>();

        // Initialize undo stacks for each participant
        for (String userId : participants) {
            undoStacks.put(userId, new ArrayDeque<>());
        }

        // Metadata
        this.lastMessageId = null;
        this.lastActivityAt = createdAt;
        this.unreadCount = new HashMap<>();

        // Initialize unread count to 0 for all participants
        for (String userId : participants) {
            unreadCount.put(userId, 0);
        }
    }

    // Getters
    public String getConversationId() { return conversationId; }
    public Set<String> getParticipants() { return new HashSet<>(participants); }
    public ConversationType getType() { return type; }
    public long getCreatedAt() { return createdAt; }
    public String getLastMessageId() { return lastMessageId; }
    public long getLastActivityAt() { return lastActivityAt; }
    public int getUnreadCount(String userId) { return unreadCount.getOrDefault(userId, 0); }
    public int getTotalMessageCount() { return history.size(); }
    public int getQueueSize() { return messageQueue.size(); }

    /**
     * Send message in conversation
     * Time: O(log n) for indexing
     *
     * Flow:
     * 1. Validate sender is participant
     * 2. Add to message queue
     * 3. Process queue immediately (or async in production)
     * 4. Add to undo stack
     */
    public synchronized void sendMessage(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }

        // Validate sender is participant
        if (!participants.contains(message.getSenderId())) {
            throw new IllegalArgumentException(
                    "Sender is not a participant in this conversation");
        }

        // Add to queue
        messageQueue.offer(message);

        // Process queue (in production: async/batch processing)
        processMessageQueue();
    }

    /**
     * Process message queue
     * Moves messages from queue to history
     * Updates status: PENDING → SENT → DELIVERED
     */
    private synchronized void processMessageQueue() {
        while (!messageQueue.isEmpty()) {
            Message message = messageQueue.poll();

            // Update status to SENT
            message.updateStatus(MessageStatus.SENT);

            // Add to history
            history.addMessage(message);

            // Add to sender's undo stack
            addToUndoStack(message.getSenderId(), message);

            // Update metadata
            lastMessageId = message.getMessageId();
            lastActivityAt = message.getTimestamp();

            // Increment unread count for all participants except sender
            for (String userId : participants) {
                if (!userId.equals(message.getSenderId())) {
                    unreadCount.put(userId, unreadCount.get(userId) + 1);
                }
            }

            // Update status to DELIVERED (simulated)
            message.updateStatus(MessageStatus.DELIVERED);
        }
    }

    /**
     * Add message to user's undo stack
     * Maintains bounded stack (max 50 messages)
     */
    private void addToUndoStack(String userId, Message message) {
        Deque<Message> stack = undoStacks.get(userId);
        if (stack == null) return;

        stack.push(message);

        // Maintain size limit
        while (stack.size() > MAX_UNDO_STACK) {
            stack.removeLast();  // Remove oldest
        }
    }

    /**
     * Undo last message sent by user
     * Time: O(1)
     *
     * Conditions:
     * - User must be sender
     * - Message sent within last 5 minutes
     * - Message not yet read
     *
     * @return true if undone, false otherwise
     */
    public synchronized boolean undoLastMessage(String userId) {
        Deque<Message> stack = undoStacks.get(userId);
        if (stack == null || stack.isEmpty()) {
            System.out.println("⚠️  No messages to undo");
            return false;
        }

        Message lastMessage = stack.peek();

        // Check if can undo
        if (!lastMessage.canUndo()) {
            System.out.println("⚠️  Cannot undo: Message too old or already read");
            stack.pop();  // Remove from stack anyway (can't undo anymore)
            return false;
        }

        // Pop from stack
        stack.pop();

        // Soft delete in history
        boolean deleted = history.markAsDeleted(lastMessage.getMessageId());

        if (deleted) {
            System.out.println("✅ Message undone: " + lastMessage.getMessageId());

            // Decrement unread count for recipients
            for (String participantId : participants) {
                if (!participantId.equals(userId)) {
                    int current = unreadCount.get(participantId);
                    unreadCount.put(participantId, Math.max(0, current - 1));
                }
            }

            return true;
        }

        return false;
    }

    /**
     * Get recent messages
     * Time: O(n) where n = count
     */
    public List<Message> getRecentMessages(int count) {
        return history.getRecentMessages(count);
    }

    /**
     * Get all messages (excluding deleted)
     * Time: O(n)
     */
    public List<Message> getAllMessages() {
        return history.getAllMessages();
    }

    /**
     * Get message by ID
     * Time: O(1)
     */
    public Message getMessageById(String messageId) {
        return history.getMessageById(messageId);
    }

    /**
     * Search messages by keyword
     * Time: O(1) lookup + O(k) results
     */
    public Set<Message> searchMessages(String keyword) {
        return history.searchByContent(keyword);
    }

    /**
     * Search messages with multiple keywords (AND)
     * Time: O(n × k) where n = keywords, k = smallest result
     */
    public Set<Message> searchMessagesAll(String... keywords) {
        return history.searchByAllKeywords(keywords);
    }

    /**
     * Get messages by sender
     * Time: O(n)
     */
    public List<Message> getMessagesBySender(String senderId) {
        return history.getMessagesBySender(senderId);
    }

    /**
     * Get messages in time range
     * Time: O(log n + k)
     */
    public List<Message> getMessagesByTimeRange(long startTime, long endTime) {
        return history.getMessagesByTimeRange(startTime, endTime);
    }

    /**
     * Mark messages as read by user
     * Updates unread count and message status
     */
    public synchronized void markAsRead(String userId) {
        if (!participants.contains(userId)) {
            throw new IllegalArgumentException("User is not a participant");
        }

        // Reset unread count
        unreadCount.put(userId, 0);

        // Update recent undelivered messages to READ status
        List<Message> recentMessages = history.getRecentMessages(10);
        for (Message msg : recentMessages) {
            if (!msg.getSenderId().equals(userId) &&
                    msg.getStatus() == MessageStatus.DELIVERED) {
                msg.updateStatus(MessageStatus.READ);
            }
        }
    }

    /**
     * Check if user is participant
     */
    public boolean isParticipant(String userId) {
        return participants.contains(userId);
    }

    /**
     * Get other participant (for one-to-one chats)
     *
     * @param userId Current user ID
     * @return Other user's ID
     * @throws IllegalStateException if not one-to-one
     */
    public String getOtherParticipant(String userId) {
        if (type != ConversationType.ONE_TO_ONE) {
            throw new IllegalStateException("Not a one-to-one conversation");
        }

        for (String participantId : participants) {
            if (!participantId.equals(userId)) {
                return participantId;
            }
        }

        throw new IllegalArgumentException("User not found in conversation");
    }

    /**
     * Get conversation age (in milliseconds)
     */
    public long getAge() {
        return System.currentTimeMillis() - createdAt;
    }

    /**
     * Get time since last activity (in milliseconds)
     */
    public long getTimeSinceLastActivity() {
        return System.currentTimeMillis() - lastActivityAt;
    }
    

    /**
     * Format duration for display
     */
    private String formatDuration(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) return days + " days";
        if (hours > 0) return hours + " hours";
        if (minutes > 0) return minutes + " minutes";
        return seconds + " seconds";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Conversation)) return false;
        Conversation that = (Conversation) o;
        return conversationId.equals(that.conversationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conversationId);
    }

    @Override
    public String toString() {
        return String.format("Conversation{id='%s', type=%s, participants=%d, messages=%d}",
                conversationId, type, participants.size(), history.size());
    }
}
