package Day2;

class DynamicArrayList<T> {
    private static final int DEFAULT_CAPACITY = 10;
    private static final float GROWTH_FACTOR = 1.5f; //growth factor correct to 1.5
    private static final float SHRINK_FACTOR = 0.25f; // shrink factor is added.
    private static final int MIN_CAPACITY = 10;

    private Object[] array;
    private int size;

    public DynamicArrayList() {
        this(DEFAULT_CAPACITY);
    }

    public DynamicArrayList(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.array = new Object[initialCapacity];
        this.size = 0;
    }

    private void resize(int newCapacity) {
        Object[] newArray = new Object[newCapacity];
        System.arraycopy(array, 0, newArray, 0, size);
        array = newArray;
    }

    private void ensureCapacity() {
        if (size == array.length) {
            resize((int) (array.length * GROWTH_FACTOR));
        }
    }

    private void shrinkIfNeeded() {
        if (array.length > MIN_CAPACITY && size < array.length * SHRINK_FACTOR) {
            resize(Math.max(MIN_CAPACITY, (int) (array.length / GROWTH_FACTOR)));
        }
    }

    public void add(T element) {
        ensureCapacity();
        array[size++] = element;
    }

    @SuppressWarnings("unchecked")
    public T remove(int index) {
        checkIndex(index);

        T removedElement = (T) array[index];

        // Shift elements left
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(array, index + 1, array, index, numMoved);
        }


        array[--size] = null;


        shrinkIfNeeded();

        return removedElement;
    }

    @SuppressWarnings("unchecked")
    public T get(int index) {
        checkIndex(index);
        return (T) array[index];
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return array.length;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(
                    String.format("Index: %d, Size: %d", index, size)
            );
        }
    }

    public void clear() {
        // Clear references for GC
        for (int i = 0; i < size; i++) {
            array[i] = null;
        }
        size = 0;
        //Memory management reuse exiting array if capacity is reasonable
        if (array.length > DEFAULT_CAPACITY * 4) {
            array = new Object[DEFAULT_CAPACITY];
        }
    }
}

class VideoPacket {
    int packetId;
    int sequence;
    byte[] data;
    long timestamp;

    public VideoPacket(int packetId, int sequence, int dataSize) {
        this.packetId = packetId;
        this.sequence = sequence;
        this.data = new byte[dataSize];
        this.timestamp = System.currentTimeMillis();
    }
    public int getSequence() {
        return sequence;
    }

    @Override
    public String toString() {
        return String.format("Packet[id=%d, seq=%d, size=%d, ts=%d]",
                packetId, sequence, data.length, timestamp);
    }
}

public class DynamicArrays {
    public static void main(String[] args) {
        DynamicArrayList<VideoPacket> packets = new DynamicArrayList<VideoPacket>();
        packets.add(new VideoPacket(291,1,1024));
        packets.add(new VideoPacket(491,2,1024));
        packets.add(new VideoPacket(211,3,1024));
        packets.add(new VideoPacket(231,4,1024));
        packets.add(new VideoPacket(261,5,1024));


        packets.remove(0);
        packets.remove(2);

        packets.clear();

    }
}
