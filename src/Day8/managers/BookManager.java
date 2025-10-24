package Day8.managers;

import Day8.entities.Book;
import Day6_7.datastructures.Trie;

import java.util.*;

public class BookManager {
    private HashMap<String, Book> indexISBN;
    private Trie<String> trieTitle;           // Store ISBN in trie values
    private Trie<String> trieAuthor;          // Store ISBN in trie values
    private HashMap<String, List<String>> indexCategory;

    public BookManager() {
        this.indexISBN = new HashMap<>();
        this.trieTitle = new Trie<>();
        this.trieAuthor = new Trie<>();
        this.indexCategory = new HashMap<>();
    }

     // Time Complexity: O(m + n) where m = title length, n = author length
    public boolean addBook(Book book) {
        if (indexISBN.containsKey(book.getISBN().toLowerCase())) {
            return false; // Book already exists
        }

        String isbnKey = book.getISBN().toLowerCase();

        // Add to primary index
        indexISBN.put(isbnKey, book);

        // Add to Trie indexes
        trieTitle.insert(book.getTitle(), isbnKey);
        trieAuthor.insert(book.getAuthor(), isbnKey);

        // Add to category index
        indexCategory.computeIfAbsent(book.getCategory().toLowerCase(),
                k -> new ArrayList<>()).add(isbnKey);

        return true;
    }

     // Time Complexity: O(m + n + k) where m = title length, n = author length, k = category size

    public boolean removeBook(String isbn) {
        String isbnKey = isbn.toLowerCase();
        if (!indexISBN.containsKey(isbnKey)) {
            return false;
        }

        Book removedBook = indexISBN.remove(isbnKey);

        // Remove from Trie indexes
        trieTitle.delete(removedBook.getTitle(), isbnKey);
        trieAuthor.delete(removedBook.getAuthor(), isbnKey);

        // Remove from category index
        List<String> categoryList = indexCategory.get(removedBook.getCategory().toLowerCase());
        if (categoryList != null) {
            categoryList.remove(isbnKey);
            if (categoryList.isEmpty()) {
                indexCategory.remove(removedBook.getCategory().toLowerCase());
            }
        }

        return true;
    }

     // Time Complexity: O(1)

    public Book getBookByISBN(String isbn) {
        return indexISBN.get(isbn.toLowerCase());
    }

     // Time Complexity: O(m) where m = title length
    public List<Book> getBookByTitle(String title) {
        Set<String> isbns = new Trie().search(title);
        List<Book> books = new ArrayList<>();
        for (String isbn : isbns) {
            Book book = indexISBN.get(isbn);
            if (book != null) {
                books.add(book);
            }
        }
        return books;
    }

     // Time Complexity O(m + k) where m = prefix length, k = number of results

    public List<Book> searchTitleByPrefix(String prefix) {
        List<String> isbns = trieTitle.searchByPrefix(prefix);
        List<Book> books = new ArrayList<>();
        for (String isbn : isbns) {
            Book book = indexISBN.get(isbn);
            if (book != null) {
                books.add(book);
            }
        }
        return books;
    }


    // Time Complexity: O(n) where n = author name length

    public List<Book> getBookByAuthor(String author) {
        Set<String> isbns = trieAuthor.search(author);
        List<Book> books = new ArrayList<>();
        for (String isbn : isbns) {
            Book book = indexISBN.get(isbn);
            if (book != null) {
                books.add(book);
            }
        }
        return books;
    }


     // Time Complexity O(n + k) where n = prefix length k = number of results

    public List<Book> searchAuthorByPrefix(String prefix) {
        List<String> isbns = trieAuthor.searchByPrefix(prefix);
        List<Book> books = new ArrayList<>();
        for (String isbn : isbns) {
            Book book = indexISBN.get(isbn);
            if (book != null) {
                books.add(book);
            }
        }
        return books;
    }


     // Get books by category

    public List<Book> getBookByCategory(String category) {
        List<String> isbnList = indexCategory.get(category.toLowerCase());
        if (isbnList == null) {
            return new ArrayList<>();
        }

        List<Book> bookList = new ArrayList<>();
        for (String isbn : isbnList) {
            Book book = indexISBN.get(isbn);
            if (book != null) {
                bookList.add(book);
            }
        }
        return bookList;
    }


     // Get all books in catalog

    public List<Book> getAllBooks() {
        return new ArrayList<>(indexISBN.values());
    }


     // Time Complexity: O(1)

    public int getTotalBooks() {
        return indexISBN.size();
    }


     // Check if book exists

    public boolean existsBook(String isbn) {
        return indexISBN.containsKey(isbn.toLowerCase());
    }

}