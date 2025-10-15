package Day3;

import java.util.Scanner;
import java.util.Iterator;
import java.util.NoSuchElementException;

class LinkedList<T> implements Iterable<T> {


    private static class Node<T> {
        T data;
        Node<T> next;
        Node<T> prev;

        Node(T data) {
            this.data = data;
            this.next = null;
            this.prev = null;
        }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public LinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public int size() {
        return size;
    }

    //used to implement DRY
    private void initializeFirstNode(Node<T> newNode) {
        head = newNode;
        tail = newNode;
    }


     // Time Complexity: O(1)
    public void addFirst(T data) {
        Node<T> newNode = new Node<>(data);

        if (isEmpty()) {
            initializeFirstNode(newNode);
        } else {
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
        }

        size++;
    }



    // Time Complexity: O(1)

    public void addLast(T data) {
        Node<T> newNode = new Node<>(data);

        if (isEmpty()) {
            initializeFirstNode(newNode);
        } else {
            newNode.prev = tail;
            tail.next = newNode;
            tail = newNode;
        }

        size++;
    }

    // Time Complexity: O(1)
    public T removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException("List is empty");
        }

        T data = head.data;
        Node<T> oldHead = head;

        head = head.next;

        if (head != null) {
            head.prev = null;
        } else {
            tail = null;
        }

        // Cleared all references to help GC
        oldHead.next = null;
        oldHead.prev = null;
        oldHead.data = null;

        size--;
        return data;
    }

    // Time Complexity: O(1)
    public T removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException("List is empty");
        }

        T data = tail.data;
        Node<T> oldTail = tail;

        tail = tail.prev;

        if (tail != null) {
            tail.next = null;
        } else {
            head = null;
        }


        oldTail.next = null;
        oldTail.prev = null;
        oldTail.data = null;

        size--;
        return data;
    }


     // Display all elements
    public void display() {
        if (isEmpty()) {
            System.out.println("Playlist is empty");
            return;
        }

        System.out.println("\n" + "═".repeat(70));
        System.out.println("🎵 CURRENT PLAYLIST (" + size + " songs)");
        System.out.println("═".repeat(70));

        int position = 1;
        for (T item : this) {
            System.out.printf("%2d. %s\n", position++, item);
        }

        System.out.println("═".repeat(70) + "\n");
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private Node<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T data = current.data;
                current = current.next;
                return data;
            }
        };
    }


    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }
}


class Song {
    private final long id;
    private final String name;
    private final String artist;

    Song(long id, String name, String artist) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be positive");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (artist == null || artist.trim().isEmpty()) {
            throw new IllegalArgumentException("Artist cannot be empty");
        }

        this.id = id;
        this.name = name.trim();
        this.artist = artist.trim();
    }


    @Override
    public String toString() {
        return String.format("[ID: %d] %s - %s", id, name, artist);
    }
}


public class MusicPlaylist {
    static void displayMenu() {
        System.out.println("MUSIC PLAYLIST");
        System.out.println("1. Add song to playlist");
        System.out.println("2. Add song to top");
        System.out.println("3. View playlist");
        System.out.println("4. Remove song from top");
        System.out.println("5. Remove song from end");
        System.out.println("6. Exit");
        System.out.print("Enter your choice: ");
    }


    private static Song createSong(Scanner sc, long songId) {
        System.out.print("Enter song name: ");
        String songName = sc.nextLine().trim();
        System.out.print("Enter artist name: ");
        String artistName = sc.nextLine().trim();

        if (songName.isEmpty() || artistName.isEmpty()) {
            System.out.println("Song name and artist cannot be empty.");
            return null;
        }

        return new Song(songId, songName, artistName);
    }
    private static long addSongToEnd(Scanner sc, LinkedList<Song> playlist, long songId) {
        Song newSong = createSong(sc, songId);
        if (newSong == null) {
            return songId;
        }
        playlist.addLast(newSong);
        System.out.println("Song added to end of playlist");

        return songId + 1;
    }

    private static long addSongToTop(Scanner sc, LinkedList<Song> playlist, long songId) {
        Song newSong = createSong(sc, songId);
        if (newSong == null) {
            return songId;
        }
        playlist.addFirst(newSong);
        System.out.println("Song added to top of playlist");

        return songId + 1;
    }

    private static void removeFromTop(LinkedList<Song> playlist) {
        Song removed = playlist.removeFirst();
        System.out.println("Removed from top: " + removed);
    }

    private static void removeFromEnd(LinkedList<Song> playlist) {
        Song removed = playlist.removeLast();
        System.out.println("Removed from end: " + removed);
    }

    public static void main(String[] args) {
        LinkedList<Song> playlist = new LinkedList<>();
        long songId = 1;
        // try with resources Guarantees Scanner is closed
        try (Scanner sc = new Scanner(System.in)) {
            System.out.println("Music Playlist started\n");
            boolean running = true;
            while (running) {
                displayMenu();
                int choice = sc.nextInt();
                sc.nextLine();

                try {
                    switch (choice) {
                        case 1:
                            songId = addSongToEnd(sc, playlist, songId);
                            break;
                        case 2:
                            songId = addSongToTop(sc, playlist, songId);
                            break;
                        case 3:
                            playlist.display();
                            break;
                        case 4:
                            removeFromTop(playlist);
                            break;
                        case 5:
                            removeFromEnd(playlist);
                            break;
                        case 6:
                            System.out.println("\n Thank you for using Music Playlist Manager");
                            running = false;
                            break;
                        default:
                            System.out.println(" Invalid choice. Please try again.");
                    }
                } catch (NoSuchElementException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }
        catch (Exception e) {

            //here scanner will close after exception happens
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
