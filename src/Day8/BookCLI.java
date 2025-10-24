package Day8;
import Day8.entities.Book;
import Day8.managers.BookManager;


import java.util.List;
import java.util.Scanner;

public class BookCLI {

    private final BookManager bookManager;

    public BookCLI() {
        this.bookManager = new BookManager();
    }

    public static void main(String[] args) {
        new BookCLI().run();
    }

    private void run() {
        System.out.println(" Welcome to the Library Catalog System");
        System.out.println("----------------------------------------");

        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                showMenu();
                int choice = readInt(scanner, "Enter your choice: ");

                switch (choice) {
                    case 1 -> addBook(scanner);
                    case 2 -> removeBook(scanner);
                    case 3 -> searchByISBN(scanner);
                    case 4 -> searchByTitle(scanner);
                    case 5 -> searchByAuthor(scanner);
                    case 6 -> searchByCategory(scanner);
                    case 7 -> searchTitleByPrefix(scanner);
                    case 8 -> searchAuthorByPrefix(scanner);
                    case 9 -> listAllBooks();
                    case 10 -> showStats();
                    case 0 -> {
                        System.out.println("Exiting...");
                        running = false;
                    }
                    default -> System.out.println("Invalid option. Please try again.");
                }
            }
        }
    }

    private void showMenu() {
        System.out.println("\n====== Library Menu ======");
        System.out.println("1. Add Book");
        System.out.println("2. Remove Book");
        System.out.println("3. Search Book by ISBN");
        System.out.println("4. Search Book by Title");
        System.out.println("5. Search Book by Author");
        System.out.println("6. Search Book by Category");
        System.out.println("7. Search Title by Prefix");
        System.out.println("8. Search Author by Prefix");
        System.out.println("9. List All Books");
        System.out.println("10. Total Books Count");
        System.out.println("0. Exit");
        System.out.println("==========================");
    }



    private void addBook(Scanner scanner) {
        System.out.println("\n--- Add Book ---");

        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine().trim();

        System.out.print("Enter Title: ");
        String title = scanner.nextLine().trim();

        System.out.print("Enter Author: ");
        String author = scanner.nextLine().trim();

        System.out.print("Enter Description: ");
        String description = scanner.nextLine().trim();

        System.out.print("Enter Category: ");
        String category = scanner.nextLine().trim();

        Book book = new Book.Bookbuilder()
                .setISBN(isbn)
                .setTitle(title)
                .setAuthor(author)
                .setDescription(description)
                .setCategory(category)
                .bookbuilder();

        if (bookManager.addBook(book)) {
            System.out.println("Book added successfully!");
        } else {
            System.out.println("Book with this ISBN already exists.");
        }
    }

    private void removeBook(Scanner scanner) {
        System.out.print("Enter ISBN to remove: ");
        String isbn = scanner.nextLine().trim();

        if (bookManager.removeBook(isbn)) {
            System.out.println("Book removed successfully.");
        } else {
            System.out.println("Book not found.");
        }
    }

    private void searchByISBN(Scanner scanner) {
        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine().trim();
        Book book = bookManager.getBookByISBN(isbn);
        printBookResult(book);
    }

    private void searchByTitle(Scanner scanner) {
        System.out.print("Enter Title: ");
        String title = scanner.nextLine().trim();
        List<Book> books = bookManager.getBookByTitle(title);
        printBookList(books);
    }

    private void searchByAuthor(Scanner scanner) {
        System.out.print("Enter Author: ");
        String author = scanner.nextLine().trim();
        List<Book> books = bookManager.getBookByAuthor(author);
        printBookList(books);
    }

    private void searchByCategory(Scanner scanner) {
        System.out.print("Enter Category: ");
        String category = scanner.nextLine().trim();
        List<Book> books = bookManager.getBookByCategory(category);
        printBookList(books);
    }

    private void searchTitleByPrefix(Scanner scanner) {
        System.out.print("Enter Title Prefix: ");
        String prefix = scanner.nextLine().trim();
        List<Book> books = bookManager.searchTitleByPrefix(prefix);
        printBookList(books);
    }

    private void searchAuthorByPrefix(Scanner scanner) {
        System.out.print("Enter Author Prefix: ");
        String prefix = scanner.nextLine().trim();
        List<Book> books = bookManager.searchAuthorByPrefix(prefix);
        printBookList(books);
    }

    private void listAllBooks() {
        System.out.println("\n📖 All Books in Catalog:");
        List<Book> allBooks = bookManager.getAllBooks();
        if (allBooks.isEmpty()) {
            System.out.println("⚠️ No books found.");
        } else {
            printBookList(allBooks);
        }
    }

    private void showStats() {
        System.out.println("\n📊 Total Books in Catalog: " + bookManager.getTotalBooks());
    }

    private void printBookResult(Book book) {
        if (book == null) {
            System.out.println("Book not found.");
        } else {
            System.out.printf("""
                    ------------------------------
                    📘 Title: %s
                    ✍️  Author: %s
                    🏷️  ISBN: %s
                    📖 Description: %s
                    📂 Category: %s
                    ------------------------------
                    """,
                    book.getTitle(),
                    book.getAuthor(),
                    book.getISBN(),
                    book.getDescription(),
                    book.getCategory());
        }
    }

    private void printBookList(List<Book> books) {
        if (books == null || books.isEmpty()) {
            System.out.println("No matching books found.");
            return;
        }
        for (Book b : books) {
            System.out.printf("""
                    ------------------------------
                    📘 Title: %s
                    ✍️  Author: %s
                    🏷️  ISBN: %s
                    📂 Category: %s
                    ------------------------------
                    """,
                    b.getTitle(), b.getAuthor(), b.getISBN(), b.getCategory());
        }
    }

    private int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }
}

