package Day6_7.managers;


import Day6_7.entities.Conversation;
import Day6_7.entities.Message;

import java.util.*;

import Day6_7.entities.ConversationType;
import Day6_7.entities.MessageType;
import Day6_7.utils.IdGenerator;


public class ConversationManager {

    // Primary storage
    private final HashMap<String, Conversation> conversations;

    //user pair → conversation ID
    private final HashMap<String, String> userPairIndex;

    //user ID → set of conversation IDs
    private final HashMap<String, Set<String>> userConversations;

    // Active conversations cache
    private final LinkedHashMap<String, Conversation> activeCache;
    private static final int MAX_ACTIVE_CACHE = 1000;


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
    }


     // Create or get one to one conversation between two users

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


        System.out.println("Created conversation: " + conversationId);

        return conversation;
    }

    // Create unique key for user pair
    private String createPairKey(String user1Id, String user2Id) {
        // Sort alphabetically to ensure consistency
        if (user1Id.compareTo(user2Id) < 0) {
            return user1Id + ":" + user2Id;
        } else {
            return user2Id + ":" + user1Id;
        }
    }


     // Add conversation to users index

    private void addToUserIndex(String userId, String conversationId) {
        userConversations
                .computeIfAbsent(userId, k -> new HashSet<>())
                .add(conversationId);
    }


     // Get conversation by ID
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


    //Get all conversations for a user O(k) where k = users conversation count
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

        return message;
    }

     // Undo last message
    public boolean undoLastMessage(String userId, String conversationId) {
        Conversation conversation = getConversation(conversationId);
        if (conversation == null) {
            System.out.println(" Conversation not found");
            return false;
        }

        return conversation.undoLastMessage(userId);
    }


     // Mark conversation as read
    public void markAsRead(String userId, String conversationId) {
        Conversation conversation = getConversation(conversationId);
        if (conversation != null) {
            conversation.markAsRead(userId);
        }
    }


     // Clear all conversations
    public void clear() {
        conversations.clear();
        userPairIndex.clear();
        userConversations.clear();
        activeCache.clear();
    }
}