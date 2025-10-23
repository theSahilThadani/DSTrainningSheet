package Day6_7.managers;


import Day6_7.entities.Conversation;
import Day6_7.entities.Message;

import java.util.*;

import Day6_7.entities.ConversationType;
import Day6_7.entities.MessageType;
import Day6_7.utils.IdGenerator;

/**
 * ConversationManager - Manages all conversations in the system
 *
 * Design:
 * - HashMap: conversationId → Conversation (O(1) lookup)
 * - HashMap: participantPair → conversationId (find by users)
 * - LinkedHashMap: LRU cache for active conversations
 * - Per-user index: userId → Set<conversationIds>
 *
 * Features:
 * - Create one-to-one conversations
 * - Find existing conversations
 * - Send/receive messages
 * - Search across all conversations
 * - Memory-efficient (LRU eviction)
 *
 * Scalability:
 * - For > 10k conversations: Implement sharding
 * - For > 100k conversations: Use external cache (Redis)
 * - Archive inactive conversations (> 30 days)
 *
 * Thread Safety:
 * - Synchronized methods for conversation creation
 * - Individual conversations handle their own synchronization
 */
public class ConversationManager {

    // Primary storage
    private final HashMap<String, Conversation> conversations;

    // Index: user pair → conversation ID (for one-to-one chats)
    private final HashMap<String, String> userPairIndex;

    // Index: user ID → set of conversation IDs
    private final HashMap<String, Set<String>> userConversations;

    // Active conversations cache (LRU)
    private final LinkedHashMap<String, Conversation> activeCache;
    private static final int MAX_ACTIVE_CACHE = 1000;

    // Statistics
    private long totalConversationsCreated;
    private long totalMessagesSent;

    public ConversationManager() {
        this.conversations = new HashMap<>();
        this.userPairIndex = new HashMap<>();
        this.userConversations = new HashMap<>();

        // LRU cache with access-order
        this.activeCache = new LinkedHashMap<String, Conversation>(
                MAX_ACTIVE_CACHE, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Conversation> eldest) {
                return size() > MAX_ACTIVE_CACHE;
            }
        };

        this.totalConversationsCreated = 0;
        this.totalMessagesSent = 0;
    }

    /**
     * Create or get one-to-one conversation between two users
     * Time: O(1)
     *
     * @param user1Id First user ID
     * @param user2Id Second user ID
     * @return Existing or new conversation
     */
    public synchronized Conversation getOrCreateConversation(String user1Id, String user2Id) {
        // Validation
        if (user1Id == null || user2Id == null) {
            throw new IllegalArgumentException("User IDs cannot be null");
        }
        if (user1Id.equals(user2Id)) {
            throw new IllegalArgumentException("Cannot create conversation with self");
        }

        // Check if conversation already exists
        String pairKey = createPairKey(user1Id, user2Id);
        String existingConvId = userPairIndex.get(pairKey);

        if (existingConvId != null) {
            Conversation existing = conversations.get(existingConvId);
            if (existing != null) {
                // Add to cache
                activeCache.put(existingConvId, existing);
                return existing;
            }
        }

        // Create new conversation
        String conversationId = IdGenerator.generateConversationId();

        Set<String> participants = new HashSet<>();
        participants.add(user1Id);
        participants.add(user2Id);

        Conversation conversation = new Conversation(
                conversationId,
                participants,
                ConversationType.ONE_TO_ONE
        );

        // Add to storage
        conversations.put(conversationId, conversation);

        // Add to pair index
        userPairIndex.put(pairKey, conversationId);

        // Add to user indexes
        addToUserIndex(user1Id, conversationId);
        addToUserIndex(user2Id, conversationId);

        // Add to active cache
        activeCache.put(conversationId, conversation);

        totalConversationsCreated++;

        System.out.println("✅ Created conversation: " + conversationId);

        return conversation;
    }

    /**
     * Create unique key for user pair (order-independent)
     * "alice" + "bob" = "bob" + "alice" (same key)
     */
    private String createPairKey(String user1Id, String user2Id) {
        // Sort alphabetically to ensure consistency
        if (user1Id.compareTo(user2Id) < 0) {
            return user1Id + ":" + user2Id;
        } else {
            return user2Id + ":" + user1Id;
        }
    }

    /**
     * Add conversation to user's index
     */
    private void addToUserIndex(String userId, String conversationId) {
        userConversations
                .computeIfAbsent(userId, k -> new HashSet<>())
                .add(conversationId);
    }

    /**
     * Get conversation by ID
     * Time: O(1)
     */
    public Conversation getConversation(String conversationId) {
        if (conversationId == null) return null;

        // Check cache first
        Conversation cached = activeCache.get(conversationId);
        if (cached != null) {
            return cached;
        }

        // Check main storage
        Conversation conversation = conversations.get(conversationId);
        if (conversation != null) {
            activeCache.put(conversationId, conversation);
        }

        return conversation;
    }

    /**
     * Find conversation between two users
     * Time: O(1)
     */
    public Conversation findConversation(String user1Id, String user2Id) {
        String pairKey = createPairKey(user1Id, user2Id);
        String conversationId = userPairIndex.get(pairKey);

        return conversationId != null ? getConversation(conversationId) : null;
    }

    /**
     * Get all conversations for a user
     * Time: O(k) where k = user's conversation count
     */
    public List<Conversation> getUserConversations(String userId) {
        Set<String> conversationIds = userConversations.get(userId);
        if (conversationIds == null || conversationIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Conversation> result = new ArrayList<>();
        for (String convId : conversationIds) {
            Conversation conv = getConversation(convId);
            if (conv != null) {
                result.add(conv);
            }
        }

        // Sort by last activity (most recent first)
        result.sort((c1, c2) -> Long.compare(
                c2.getLastActivityAt(),
                c1.getLastActivityAt()
        ));

        return result;
    }

    /**
     * Send message
     * Time: O(1) to route + O(log n) to index
     *
     * @param senderId Sender user ID
     * @param recipientId Recipient user ID
     * @param content Message content
     * @return Sent message
     */
    public Message sendMessage(String senderId, String recipientId, String content) {
        // Get or create conversation
        Conversation conversation = getOrCreateConversation(senderId, recipientId);

        // Create message
        String messageId = IdGenerator.generateMessageId();
        Message message = new Message(
                messageId,
                senderId,
                conversation.getConversationId(),
                content,
                MessageType.TEXT
        );

        // Send in conversation
        conversation.sendMessage(message);

        totalMessagesSent++;

        return message;
    }

    /**
     * Undo last message
     * Time: O(1)
     */
    public boolean undoLastMessage(String userId, String conversationId) {
        Conversation conversation = getConversation(conversationId);
        if (conversation == null) {
            System.out.println("❌ Conversation not found");
            return false;
        }

        return conversation.undoLastMessage(userId);
    }

    /**
     * Mark conversation as read
     * Time: O(1)
     */
    public void markAsRead(String userId, String conversationId) {
        Conversation conversation = getConversation(conversationId);
        if (conversation != null) {
            conversation.markAsRead(userId);
        }
    }

    /**
     * Search messages across all user's conversations
     * Time: O(c × k) where c = conversations, k = matches per conversation
     */
    public Map<Conversation, Set<Message>> searchAllConversations(String userId, String keyword) {
        List<Conversation> userConvs = getUserConversations(userId);
        Map<Conversation, Set<Message>> results = new HashMap<>();

        for (Conversation conv : userConvs) {
            Set<Message> matches = conv.searchMessages(keyword);
            if (!matches.isEmpty()) {
                results.put(conv, matches);
            }
        }

        return results;
    }

    /**
     * Get total unread count for user (across all conversations)
     * Time: O(k) where k = user's conversations
     */
    public int getTotalUnreadCount(String userId) {
        List<Conversation> userConvs = getUserConversations(userId);

        int total = 0;
        for (Conversation conv : userConvs) {
            total += conv.getUnreadCount(userId);
        }

        return total;
    }

    /**
     * Get recent activity (conversations with recent messages)
     * Time: O(k log k) where k = user's conversations (for sorting)
     */
    public List<Conversation> getRecentActivity(String userId, int limit) {
        List<Conversation> userConvs = getUserConversations(userId);

        // Already sorted by last activity in getUserConversations()

        // Return top N
        int actualLimit = Math.min(limit, userConvs.size());
        return userConvs.subList(0, actualLimit);
    }

    /**
     * Archive old conversations (inactive for > 30 days)
     * Time: O(n)
     *
     * In production: Move to cold storage
     */
    public int archiveOldConversations(int daysInactive) {
        long cutoffTime = System.currentTimeMillis() - (daysInactive * 24L * 60 * 60 * 1000);

        List<String> toArchive = new ArrayList<>();

        for (Conversation conv : conversations.values()) {
            if (conv.getLastActivityAt() < cutoffTime) {
                toArchive.add(conv.getConversationId());
            }
        }

        // Archive (for now, just remove from active storage)
        for (String convId : toArchive) {
            conversations.remove(convId);
            activeCache.remove(convId);
            // In production: Move to database/file storage
        }

        System.out.println("📦 Archived " + toArchive.size() + " inactive conversations");

        return toArchive.size();
    }

    /**
     * Display statistics
     */
    public void displayStatistics() {
        System.out.println("\n" + "═".repeat(80));
        System.out.println("📊 CONVERSATION MANAGER STATISTICS");
        System.out.println("═".repeat(80));
        System.out.printf("Total conversations:          %,d\n", conversations.size());
        System.out.printf("Active cache size:            %,d / %,d\n",
                activeCache.size(), MAX_ACTIVE_CACHE);
        System.out.printf("Total created (lifetime):     %,d\n", totalConversationsCreated);
        System.out.printf("Total messages sent:          %,d\n", totalMessagesSent);

        // Calculate total messages across all conversations
        int totalMessages = 0;
        for (Conversation conv : conversations.values()) {
            totalMessages += conv.getTotalMessageCount();
        }
        System.out.printf("Total messages in storage:    %,d\n", totalMessages);

        // Average messages per conversation
        double avgMessages = conversations.isEmpty() ? 0 :
                (double) totalMessages / conversations.size();
        System.out.printf("Avg messages per conversation: %.2f\n", avgMessages);

        // Memory estimate
        long estimatedMemory = conversations.size() * 50_000; // ~50KB per conversation
        System.out.printf("Estimated memory:             %.2f MB\n",
                estimatedMemory / (1024.0 * 1024.0));

        System.out.println("═".repeat(80) + "\n");
    }

    /**
     * Display detailed user statistics
     */
    public void displayUserStatistics(String userId) {
        System.out.println("\n" + "═".repeat(80));
        System.out.println("👤 USER STATISTICS: " + userId);
        System.out.println("═".repeat(80));

        List<Conversation> userConvs = getUserConversations(userId);
        System.out.printf("Total conversations:    %d\n", userConvs.size());
        System.out.printf("Total unread messages:  %d\n", getTotalUnreadCount(userId));

        // Count messages sent by user
        int messagesSent = 0;
        int messagesReceived = 0;

        for (Conversation conv : userConvs) {
            List<Message> messages = conv.getAllMessages();
            for (Message msg : messages) {
                if (msg.getSenderId().equals(userId)) {
                    messagesSent++;
                } else {
                    messagesReceived++;
                }
            }
        }

        System.out.printf("Messages sent:          %d\n", messagesSent);
        System.out.printf("Messages received:      %d\n", messagesReceived);

        System.out.println("\nRecent conversations:");
        List<Conversation> recent = getRecentActivity(userId, 5);
        for (int i = 0; i < recent.size(); i++) {
            Conversation conv = recent.get(i);
            String otherUser = conv.getOtherParticipant(userId);
            int unread = conv.getUnreadCount(userId);

            System.out.printf("  %d. %s (%d messages, %d unread)\n",
                    i + 1, otherUser, conv.getTotalMessageCount(), unread);
        }

        System.out.println("═".repeat(80) + "\n");
    }

    /**
     * Get total conversation count
     */
    public int getTotalConversationCount() {
        return conversations.size();
    }

    /**
     * Get total message count across all conversations
     */
    public int getTotalMessageCount() {
        int total = 0;
        for (Conversation conv : conversations.values()) {
            total += conv.getTotalMessageCount();
        }
        return total;
    }

    /**
     * Clear all conversations (for testing)
     */
    public void clear() {
        conversations.clear();
        userPairIndex.clear();
        userConversations.clear();
        activeCache.clear();
    }
}