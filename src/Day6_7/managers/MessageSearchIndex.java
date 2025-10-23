package Day6_7.managers;

import Day6_7.entities.Message;

import java.util.*;


public class MessageSearchIndex {


    private final HashMap<String, Set<Message>> wordIndex;


    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "a", "an", "and", "are", "as", "at", "be", "but", "by",
            "for", "if", "in", "is", "it", "of", "on", "or", "the", "to"
    ));

    private boolean useStopWords;
    private int minWordLength;


    public MessageSearchIndex() {
        this(true, 2);
    }


    //create search index with custom settings
    public MessageSearchIndex(boolean useStopWords, int minWordLength) {
        this.wordIndex = new HashMap<>();
        this.useStopWords = useStopWords;
        this.minWordLength = minWordLength;
    }

    //index a message
    public void indexMessage(Message message) {
        if (message == null || message.isDeleted()) {
            return;
        }

        // Tokenize content into words
        Set<String> words = tokenize(message.getContent());

        // Add to index
        for (String word : words) {
            wordIndex
                    .computeIfAbsent(word, k -> new HashSet<>())
                    .add(message);
        }
    }


    //Remove message from index in o(W)
    public void removeMessage(Message message) {
        if (message == null) return;

        Set<String> words = tokenize(message.getContent());

        for (String word : words) {
            Set<Message> messages = wordIndex.get(word);
            if (messages != null) {
                messages.remove(message);
                // Clean up empty sets
                if (messages.isEmpty()) {
                    wordIndex.remove(word);
                }
            }
        }
    }

    //search message from word gives back string
    public Set<Message> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptySet();
        }

        String normalized = normalizeWord(keyword);

        // Filter out if it's a stop word or too short
        if (shouldSkipWord(normalized)) {
            return Collections.emptySet();
        }

        Set<Message> results = wordIndex.get(normalized);

        // Return copy to prevent external modification
        return results != null
                ? new HashSet<>(results)
                : Collections.emptySet();
    }

    public Set<Message> searchAll(String... keywords) {
        if (keywords == null || keywords.length == 0) {
            return Collections.emptySet();
        }

        // Find smallest result set (optimization)
        Set<Message> smallest = null;
        int minSize = Integer.MAX_VALUE;

        for (String keyword : keywords) {
            Set<Message> results = search(keyword);
            if (results.isEmpty()) {
                return Collections.emptySet();  // Short-circuit
            }
            if (results.size() < minSize) {
                smallest = results;
                minSize = results.size();
            }
        }

        // Intersect with other result sets
        Set<Message> finalResults = new HashSet<>(smallest);

        for (String keyword : keywords) {
            Set<Message> results = search(keyword);
            finalResults.retainAll(results);  // Intersection

            if (finalResults.isEmpty()) {
                break;  // Short-circuit
            }
        }

        return finalResults;
    }


    //this will search any from given words like hi or hey
    public Set<Message> searchAny(String... keywords) {
        if (keywords == null || keywords.length == 0) {
            return Collections.emptySet();
        }

        Set<Message> results = new HashSet<>();

        for (String keyword : keywords) {
            results.addAll(search(keyword));
        }

        return results;
    }


    private Set<String> tokenize(String text) {
        if (text == null) {
            return Collections.emptySet();
        }

        Set<String> words = new HashSet<>();


        String normalized = text.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", " ")  // Remove punctuation
                .trim();

        // Split on whitespace
        String[] tokens = normalized.split("\\s+");

        for (String token : tokens) {
            String word = token.trim();

            if (!shouldSkipWord(word)) {
                words.add(word);
            }
        }

        return words;
    }


    private String normalizeWord(String word) {
        return word.toLowerCase().trim();
    }


    private boolean shouldSkipWord(String word) {
        if (word.isEmpty()) {
            return true;
        }

        if (word.length() < minWordLength) {
            return true;
        }

        if (useStopWords && STOP_WORDS.contains(word)) {
            return true;
        }

        return false;
    }

    public Set<String> getAllWords() {
        return new HashSet<>(wordIndex.keySet());
    }

    public int getWordCount() {
        return wordIndex.size();
    }

    public void clear() {
        wordIndex.clear();
    }

    public int size() {
        return wordIndex.size();
    }

    public boolean isEmpty() {
        return wordIndex.isEmpty();
    }
}
