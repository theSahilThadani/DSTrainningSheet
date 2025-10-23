package Day6_7.datastructures;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TrieNode<T> {
    private final Map<Character, TrieNode<T>> children;
    private final Set<T> values;
    private boolean isEndOfWord;
    public TrieNode() {
        this.children = new HashMap<>();
        this.values = new HashSet<>();
        this.isEndOfWord = false;
    }
    public Map<Character, TrieNode<T>> getChildren() {
        return children;
    }

    public Set<T> getValues() {
        return values;
    }

    public boolean isEndOfWord() {
        return isEndOfWord;
    }

    public void setEndOfWord(boolean endOfWord) {
        isEndOfWord = endOfWord;
    }

    public void addValue(T value) {
        values.add(value);
    }

    public void removeValue(T value) {
        values.remove(value);
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public boolean hasValues() {
        return !values.isEmpty();
    }
}
