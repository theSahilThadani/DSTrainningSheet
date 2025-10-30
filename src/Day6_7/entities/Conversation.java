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


     // Create new conversation
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


     // Send message in conversation O(log n) for indexing
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

        // Process queue
        processMessageQueue();
    }


    private synchronized void processMessageQueue() {
        while (!messageQueue.isEmpty()) {
            Message message = messageQueue.poll();

            // Update status to SENT
            message.updateStatus(MessageStatus.SENT);

            // Add to history
            history.addMessage(message);

            // Add to senders undo stack
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

            // Update status to DELIVERED
            message.updateStatus(MessageStatus.DELIVERED);
        }
    }

    private void addToUndoStack(String userId, Message message) {
        Deque<Message> stack = undoStacks.get(userId);
        if (stack == null) return;

        stack.push(message);

        // Maintain size limit
        while (stack.size() > MAX_UNDO_STACK) {
            stack.removeLast();  // Remove oldest
        }
    }


    public synchronized boolean undoLastMessage(String userId) {
        Deque<Message> stack = undoStacks.get(userId);
        if (stack == null || stack.isEmpty()) {
            System.out.println("⚠No messages to undo");
            return false;
        }

        Message lastMessage = stack.peek();

        // Check if you can undo
        if (!lastMessage.canUndo()) {
            System.out.println("  Cannot undo: Message too old or already read");
            stack.pop();  // Remove from stack anyway (can't undo anymore)
            return false;
        }

        // Pop from stack
        stack.pop();

        // Soft delete in history
        boolean deleted = history.markAsDeleted(lastMessage.getMessageId());

        if (deleted) {
            System.out.println(" Message undone: " + lastMessage.getMessageId());

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


    public List<Message> getRecentMessages(int count) {
        return history.getRecentMessages(count);
    }


    public List<Message> getAllMessages() {
        return history.getAllMessages();
    }


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

    public long getTimeSinceLastActivity() {
        return System.currentTimeMillis() - lastActivityAt;
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
