package Day6_7.datastructures;

import java.util.*;

public class Trie<T> {
    private final TrieNode<T> root;
    private int size;

    public Trie() {
        root = new TrieNode<>();
        size = 0;
    }

    public void insert(String word, T value) {
        if(word == null || word.isEmpty()) throw new IllegalArgumentException("Word cannot be null or empty");

        word = word.toLowerCase();

        TrieNode<T> current = root;
        for(char c : word.toCharArray()) {
            current.getChildren().putIfAbsent(c, new TrieNode<>());
            current = current.getChildren().get(c);
        }
        if(!current.isEndOfWord()){
            current.setEndOfWord(true);
            size++;
        }
        current.addValue(value);
    }

    public Set<T> search(String word) {
        if (word == null || word.isEmpty()) {
            return Collections.emptySet();
        }
        word = word.toLowerCase();
        TrieNode<T> node = findNode(word);
        if(node != null && node.isEndOfWord()){
            return new HashSet<>(node.getValues());
        }
        return Collections.emptySet();
    }


    public List<T> searchByPrefix(String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return Collections.emptyList();
        }

        prefix = prefix.toLowerCase();
        TrieNode<T> node = findNode(prefix);

        if (node == null) {
            return Collections.emptyList();
        }

        List<T> results = new ArrayList<>();
        collectAllValues(node, results);
        return results;
    }

    public TrieNode<T> findNode(String word) {
        TrieNode<T> current = root;
        for(char c : word.toCharArray()) {
            TrieNode<T> next = current.getChildren().get(c);
            if(next == null) return null;
            current = next;
        }
        return current;
    }

    private void collectAllValues(TrieNode<T> node, List<T> results) {
        if (node == null) return;

        if (node.isEndOfWord()) {
            results.addAll(node.getValues());
        }

        for (TrieNode<T> child : node.getChildren().values()) {
            collectAllValues(child, results);
        }
    }

    public boolean delete(String word, T value) {
        if (word == null || word.isEmpty()) {
            return false;
        }

        word = word.toLowerCase();
        return deleteHelper(root, word, value, 0);
    }

    private boolean deleteHelper(TrieNode<T> current, String word, T value, int index) {
        if (index == word.length()) {
            // Reached end of word
            if (!current.isEndOfWord()) {
                return false; // Word doesn't exist
            }

            current.removeValue(value);

            if (!current.hasValues()) {
                current.setEndOfWord(false);
                size--;
            }

            // Delete node if no children and not end of another word
            return !current.hasChildren() && !current.isEndOfWord();
        }

        char ch = word.charAt(index);
        TrieNode<T> next = current.getChildren().get(ch);

        if (next == null) {
            return false; // Word doesn't exist
        }

        boolean shouldDeleteChild = deleteHelper(next, word, value, index + 1);

        if (shouldDeleteChild) {
            current.getChildren().remove(ch);
            // Delete current node if no children and not end of word
            return !current.hasChildren() && !current.isEndOfWord();
        }

        return false;
    }


     // Check if word exists

    public boolean contains(String word) {
        if (word == null || word.isEmpty()) {
            return false;
        }

        word = word.toLowerCase();
        TrieNode<T> node = findNode(word);
        return node != null && node.isEndOfWord();
    }


    public boolean startsWith(String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return false;
        }

        prefix = prefix.toLowerCase();
        return findNode(prefix) != null;
    }

     // Get number of words in Trie
    public int size() {
        return size;
    }

     // Check if Trie is empty
    public boolean isEmpty() {
        return size == 0;
    }


    public void clear() {
        root.getChildren().clear();
        size = 0;
    }

     // Get all words in Trie
    public List<T> getAllValues() {
        List<T> results = new ArrayList<>();
        collectAllValues(root, results);
        return results;
    }
}
