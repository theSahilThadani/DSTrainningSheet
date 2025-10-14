package Day2;

class DyanamicArrayList<T>{
    T [] array;
    int size;
    public DyanamicArrayList(){
        array = (T[]) new Object[1000];
        size = 0;
    }

    public void add(T t){
        if(size == array.length){
            T[] temp = (T[]) new Object[array.length*2];
            System.arraycopy(array, 0, temp, 0, array.length);
            array = temp;
        }
        array[size] = t;
        size++;
    }

    public T remove(int index){
        checkIndex(index);
        T temp = (T)array[index];
        for(int i = index; i < size - 1; i++){
            array[i] = array[i + 1];
        }
        array[size - 1] = null;
        size--;
        return temp;
    }

    public T get(int index){
        checkIndex(index);
        return (T)array[index];
    }

    public int size(){
        return size;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
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
}

public class DynamicArrays {
    public static void main(String[] args) {
        DyanamicArrayList<VideoPacket> packets = new DyanamicArrayList<VideoPacket>();
        packets.add(new VideoPacket(291,1,1024));
        packets.add(new VideoPacket(491,2,1024));
        packets.add(new VideoPacket(211,3,1024));
        packets.add(new VideoPacket(231,4,1024));
        packets.add(new VideoPacket(261,5,1024));

    }
}
