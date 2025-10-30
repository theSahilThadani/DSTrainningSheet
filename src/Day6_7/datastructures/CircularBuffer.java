package Day6_7.datastructures;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class CircularBuffer<T>  implements Iterable<T> {
    private final Object[] buffer;
    private final int capacity;
    private int head;
    private int tail;
    private int size;

    //creates buffer with fixed limit.
    public CircularBuffer(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }

        this.capacity = capacity;
        this.buffer = new Object[capacity];
        this.head = 0;
        this.tail = 0;
        this.size = 0;
    }

    //add element in buffer in o(1) and override old ones.
    public void add(T element) {
        if (element == null) {
            throw new IllegalArgumentException("Null elements not allowed");
        }

        buffer[tail] = element;
        tail = (tail + 1) % capacity;  // Wrap around

        if (size < capacity) {
            size++;
        } else {
            // Buffer is full, move head forward (overwrite oldest)
            head = (head + 1) % capacity;
        }
    }

    //get element by index
    @SuppressWarnings("unchecked")
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(
                    String.format("Index: %d, Size: %d", index, size)
            );
        }

        // Calculate actual position in buffer
        int actualIndex = (head + index) % capacity;
        return (T) buffer[actualIndex];
    }


    //get the oldest element like first index [0]
    public T getFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException("Buffer is empty");
        }
        return get(0);
    }

    // het newest element in buffer last element.
    public T getLast() {
        if (isEmpty()) {
            throw new NoSuchElementException("Buffer is empty");
        }
        return get(size - 1);
    }


    //remove old element
    @SuppressWarnings("unchecked")
    public T removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException("Buffer is empty");
        }

        T element = (T) buffer[head];
        buffer[head] = null;  // Help GC
        head = (head + 1) % capacity;
        size--;

        return element;
    }

    //remove newest element form buffer
    @SuppressWarnings("unchecked")
    public T removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException("Buffer is empty");
        }

        // Move tail back
        tail = (tail - 1 + capacity) % capacity;
        T element = (T) buffer[tail];
        buffer[tail] = null;
        size--;

        return element;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == capacity;
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return capacity;
    }

    public double getLoadFactor() {
        return (double) size / capacity;
    }

    public void clear() {
        // Clear references for GC
        for (int i = 0; i < capacity; i++) {
            buffer[i] = null;
        }
        head = 0;
        tail = 0;
        size = 0;
    }

    public boolean contains(T element) {
        if (element == null) return false;

        for (T item : this) {
            if (element.equals(item)) {
                return true;
            }
        }
        return false;
    }

    //iterator implementation for loops
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < size;
            }

            @Override
            @SuppressWarnings("unchecked")
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                int actualIndex = (head + currentIndex) % capacity;
                currentIndex++;
                return (T) buffer[actualIndex];
            }
        };
    }


    //convert to array
    @SuppressWarnings("unchecked")
    public T[] toArray() {
        Object[] result = new Object[size];

        for (int i = 0; i < size; i++) {
            result[i] = get(i);
        }

        return (T[]) result;
    }


    @Override
    public String toString() {
        if (isEmpty()) {
            return "CircularBuffer[]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("CircularBuffer[");

        for (int i = 0; i < size; i++) {
            sb.append(get(i));
            if (i < size - 1) {
                sb.append(", ");
            }
        }

        sb.append("] (size=").append(size)
                .append(", capacity=").append(capacity)
                .append(", load=").append(String.format("%.2f", getLoadFactor()))
                .append(")");

        return sb.toString();
    }
}
