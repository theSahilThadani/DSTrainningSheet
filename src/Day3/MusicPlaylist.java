package Day3;

import org.w3c.dom.Node;

import java.util.Scanner;

class node<T> {
    T data;
    node<T> next;
    node<T> prev;
    public node(T data) {
        this.data = data;
        this.next = null;
        this.prev = null;
    }
}

class LinkedList <T>{
    private node<T> head;
    private node<T> tail;
    private int size;

    LinkedList(){
        head = null;
        tail = null;
        size = 0;
    }

    public boolean isEmpty(){
        return head == null;
    }
    public int size(){
        return size;
    }
    public void add(T data){
        node<T> newNode = new node<T>(data);
        if(isEmpty()){
            tail = newNode;
            head = newNode;
        }else {
            head.prev = newNode;
            newNode.next = head;
            head = newNode;
        }

        size++;
    }
    public void addLast(T data){
        node<T> newNode = new node<>(data);

        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        size++;
    }
    public T removeFirst(){
        if(isEmpty()){
            return null;
        }
        node<T> temp = head;
        if(head.next != null){
            head.next.prev = null;
        }
        head = head.next;
        size--;
        if(isEmpty()){
            tail = null;
        }
        return temp.data;
    }
    public T removeLast(){
        if(isEmpty()){
            return null;
        }
        node<T> temp = tail;
        if(tail.prev != null){
            tail.prev.next = null;
        }
        tail = tail.prev;
        size--;
        if(isEmpty()){
            head = null;
        }
        return temp.data;
    }

    public void displayPlaylist() {
        if (isEmpty()) {
            System.out.println("\n Playlist is empty\n");
            return;
        }

        System.out.println("\n" + "=".repeat(70));
        System.out.println("CURRENT PLAYLIST (" + size + " songs)");
        System.out.println("=".repeat(70));

        node<T> current = head;
        int position = 1;
        while (current != null) {
            System.out.println(position + ". " + current.data);
            current = current.next;
            position++;
        }
        System.out.println("=".repeat(70) + "\n");
    }

}


class Song{
    long id;
    String name;
    String artist;
    Song(long id, String name, String artist){
        this.id = id;
        this.name = name;
        this.artist = artist;
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

    public static void main(String[] args) {
        LinkedList<Song> playlist = new LinkedList<>();
        Scanner sc = new Scanner(System.in);
        long songId = 1;
        boolean running = true;

        System.out.println("Music Playlist started\n");

        while (running) {
            displayMenu();
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter song name: ");
                    String songName = sc.nextLine();
                    System.out.print("Enter artist name: ");
                    String artistName = sc.nextLine();

                    Song newSong = new Song(songId, songName, artistName);
                    playlist.addLast(newSong);
                    songId++;

                    System.out.println("Song added to playlist");
                    break;

                case 2:
                    System.out.print("Enter song name: ");
                    songName = sc.nextLine();
                    System.out.print("Enter artist name: ");
                    artistName = sc.nextLine();

                    newSong = new Song(songId, songName, artistName);
                    playlist.add(newSong);
                    songId++;

                    System.out.println("Song added to playlist");
                    break;

                case 3:
                    playlist.displayPlaylist();
                    break;

                case 4:
                    if (playlist.isEmpty()) {
                        System.out.println("Playlist is empty");
                    } else {
                        Song removed = playlist.removeFirst();
                        System.out.println("Removed from top: " + removed);
                        System.out.println("removed successfully");
                    }
                    break;

                case 5:
                    if (playlist.isEmpty()) {
                        System.out.println("Playlist is empty");
                    } else {
                        Song removed = playlist.removeLast();
                        System.out.println("Removed from end: " + removed);
                        System.out.println("Song removed successfully");
                    }
                    break;

                case 6:
                    System.out.println("Thank you for using");
                    running = false;
                    break;

                default:
                    System.out.println("Invalid choice");
            }
        }

        sc.close();
    }
}
