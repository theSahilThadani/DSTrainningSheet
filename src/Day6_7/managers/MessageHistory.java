package Day6_7.managers;

import Day6_7.datastructures.CircularBuffer;
import Day6_7.entities.Message;

import java.util.*;

public class MessageHistory {
    //using buffer datastructures to store message history
    private final CircularBuffer<Message> recentMessages;
    private final HashMap<String, Message> messageById;
    private final TreeMap<Long, Message> timeIndex; //for time based retrieval
    private final MessageSearchIndex searchIndex;

    private final int capacity;
    private long totalMessagesAdded;

    // Constants
    private static final int DEFAULT_CAPACITY = 1000;

    //creating buffer of history limit of 1000;
    public MessageHistory() {
        this(DEFAULT_CAPACITY);
    }

    //with specific capacity
    public MessageHistory(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }

        this.capacity = capacity;
        this.recentMessages = new CircularBuffer<>(capacity);
        this.messageById = new HashMap<>(capacity);
        this.timeIndex = new TreeMap<>();
        this.searchIndex = new MessageSearchIndex();
        this.totalMessagesAdded = 0;
    }

    //add message will take O(logn) due to treemap
    public synchronized void addMessage(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }

        // Check for duplicates
        if (messageById.containsKey(message.getMessageId())) {
            System.out.println("⚠️  Duplicate message ignored: " + message.getMessageId());
            return;
        }

        // If buffer is full. remove oldest from indexes
        if (recentMessages.isFull()) {
            Message oldest = recentMessages.getFirst();
            removeFromIndexes(oldest);
            searchIndex.removeMessage(oldest);
        }

        // Add to all storage layers
        recentMessages.add(message);
        messageById.put(message.getMessageId(), message);
        timeIndex.put(message.getTimestamp(), message);
        searchIndex.indexMessage(message);

        totalMessagesAdded++;
    }

    //remove messages from indexes
    private void removeFromIndexes(Message message) {
        messageById.remove(message.getMessageId());
        timeIndex.remove(message.getTimestamp());
        searchIndex.removeMessage(message);
    }

    //get message by Id in o(1)
    public Message getMessageById(String messageId) {
        if (messageId == null) return null;
        return messageById.get(messageId);
    }

    //get recent messages in o(n)
    public List<Message> getRecentMessages(int count) {
        if (count <= 0) {
            return Collections.emptyList();
        }

        int size = recentMessages.size();
        int actualCount = Math.min(count, size);

        List<Message> result = new ArrayList<>(actualCount);

        // Iterate from newest to oldest
        for (int i = size - 1; i >= size - actualCount; i--) {
            result.add(recentMessages.get(i));
        }

        return result;
    }

    //get all messages
    public List<Message> getAllMessages() {
        List<Message> result = new ArrayList<>(recentMessages.size());

        for (Message msg : recentMessages) {
            if (!msg.isDeleted()) {  // Skip deleted messages
                result.add(msg);
            }
        }

        return result;
    }

    //get message in time range in o(log n + k)
    public List<Message> getMessagesByTimeRange(long startTime, long endTime) {
        if (startTime > endTime) {
            throw new IllegalArgumentException("Start time must be <= end time");
        }

        SortedMap<Long, Message> rangeMap = timeIndex.subMap(startTime, endTime + 1);
        return new ArrayList<>(rangeMap.values());
    }

    //get messages after particular time.
    public List<Message> getMessagesAfter(long timestamp) {
        SortedMap<Long, Message> tailMap = timeIndex.tailMap(timestamp + 1);
        return new ArrayList<>(tailMap.values());
    }

    //get messages before specific time
    public List<Message> getMessagesBefore(long timestamp) {
        SortedMap<Long, Message> headMap = timeIndex.headMap(timestamp);
        return new ArrayList<>(headMap.values());
    }

    //search messages by liner search
    public Set<Message> searchByContent(String keyword) {
        return searchIndex.search(keyword);
    }
    //search by all keywords like hello,world includes both
    public Set<Message> searchByAllKeywords(String... keywords) {
        return searchIndex.searchAll(keywords);
    }

    //search by any word like hello or world
    public Set<Message> searchByAnyKeywords(String... keywords) {
        return searchIndex.searchAny(keywords);
    }

    //get messages by sender in o(n)
    public List<Message> getMessagesBySender(String senderId) {
        if (senderId == null) {
            return Collections.emptyList();
        }

        List<Message> results = new ArrayList<>();

        for (Message msg : recentMessages) {
            if (!msg.isDeleted() && msg.getSenderId().equals(senderId)) {
                results.add(msg);
            }
        }

        return results;
    }

    //soft delete
    public boolean markAsDeleted(String messageId) {
        Message message = messageById.get(messageId);
        if (message == null) {
            return false;
        }

        message.markAsDeleted();
        searchIndex.removeMessage(message);
        return true;
    }


    public int size() {
        return recentMessages.size();
    }

    public boolean isEmpty() {
        return recentMessages.isEmpty();
    }

    public double getLoadFactor() {
        return recentMessages.getLoadFactor();
    }

    @Override
    public String toString() {
        return String.format("MessageHistory{size=%d, capacity=%d, load=%.2f%%}",
                size(), capacity, getLoadFactor() * 100);
    }
}
