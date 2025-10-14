package Day4;


import java.util.HashMap;
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

    public void display(){
        if (isEmpty()) {
            System.out.println("\n tabs are not added\n");
            return;
        }

        System.out.println("\n" + "=".repeat(70));
        System.out.println("CURRENT OPEN TABS");
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
class Tabs{
    String name;
    Tabs(String name){
        this.name = name;
    }
    public String toString(){
        return "[ "+ name+ " ]";
    }
}
public class BrowserTabsManager {
    static void prompt(){
        System.out.println("=".repeat(50) + "\n");
        System.out.println("Welcome to Browser Tabs Manager");
        System.out.println("=".repeat(50) + "\n");
        System.out.println("1. To add Tabs");
        System.out.println("2. To close Tabs from first");
        System.out.println("3. To close Tabs from last");
        System.out.println("4. Display All Tabs");
        System.out.println("5. Search for tab");
        System.out.println("6. Exit");
    }
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        boolean running = true;
        LinkedList<Tabs> linkedList = new LinkedList<>();
        HashMap<String, Tabs> tabsMap = new HashMap<>();

        while(running){
            prompt();
            System.out.println("Enter your choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch(choice){
                case 1:
                    System.out.print("Enter Tab name: ");
                    String name = sc.nextLine();
                    Tabs tabs = new Tabs(name);
                    tabsMap.put(tabs.name.toLowerCase(), tabs);
                    linkedList.addLast(tabs);
                    break;
                case 2:
                    if (linkedList.isEmpty()) {
                        System.out.println("Tabs is empty");
                    } else {
                        Tabs removed = linkedList.removeFirst();
                        System.out.println("Removed from first: " + removed);
                    }
                    break;
                case 3:
                    if (linkedList.isEmpty()) {
                        System.out.println("Tabs is empty");
                    } else {
                        Tabs removed = linkedList.removeLast();
                        System.out.println("Removed from last: " + removed);
                    }
                    break;
                case 4:
                    linkedList.display();
                    break;
                case 5:
                    System.out.println("Please enter name of tab");
                    String tabName = sc.nextLine();
                    if(tabsMap.containsKey(tabName.toLowerCase())){
                        System.out.println("TAB FOUND");
                        System.out.println("\n" + "=".repeat(70));
                        System.out.println(tabsMap.get(tabName.toLowerCase()));
                        System.out.println("=".repeat(70));
                    }
                    else System.out.println("TAB NOT FOUND");
                    break;
                case 6:
                    running = false;
                    System.out.println("All tabs closed.");
                    break;
                default:
                    System.out.println("Invalid choice");
            }
        }


    }
}
