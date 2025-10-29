package Day8.managers;

import Day8.entities.Book;
import Day6_7.datastructures.Trie;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BookManager {
    private Map<String, Book> indexISBN;
    private Trie<String> trieTitle;           // Store ISBN in trie values
    private Trie<String> trieAuthor;          // Store ISBN in trie values
    private Map<String, List<String>> indexCategory;

    public BookManager() {
        this.indexISBN = new ConcurrentHashMap<>();
        this.trieTitle = new Trie<>();
        this.trieAuthor = new Trie<>();
        this.indexCategory = new ConcurrentHashMap<>();
    }

     // Time Complexity: O(m + n) where m = title length, n = author length
     public boolean addBook(Book book) {
         if (book == null) {
             throw new IllegalArgumentException("Book cannot be null");
         }

         // Validate ISBN
         String isbn = book.getISBN();
         if (isbn == null || isbn.trim().isEmpty()) {
             throw new IllegalArgumentException("ISBN cannot be empty");
         }
         if (!isValidISBN(isbn)) {
             throw new IllegalArgumentException("Invalid ISBN format");
         }

         // Validate title
         if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
             throw new IllegalArgumentException("Title cannot be empty");
         }

         // Validate author
         if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
             throw new IllegalArgumentException("Author cannot be empty");
         }

         // Validate category
         if (book.getCategory() == null || book.getCategory().trim().isEmpty()) {
             throw new IllegalArgumentException("Category cannot be empty");
         }

         String isbnKey = isbn.toLowerCase();
         if (indexISBN.containsKey(isbnKey)) {
             return false;
         }

         indexISBN.put(isbnKey, book);
         trieTitle.insert(book.getTitle().toLowerCase(), isbnKey);
         trieAuthor.insert(book.getAuthor().toLowerCase(), isbnKey);
         indexCategory.computeIfAbsent(book.getCategory().toLowerCase(),
                 k -> new ArrayList<>()).add(isbnKey);

         return true;
     }

    private boolean isValidISBN(String isbn) {
        // Simple validation ISBN is 10 or 13 digits
        String clean = isbn.replaceAll("[^0-9X]", "");
        return clean.length() == 10 || clean.length() == 13;
    }

     // Time Complexity: O(m + n + k) where m = title length, n = author length, k = category size
    public boolean removeBook(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return false;
        }
        String isbnKey = isbn.toLowerCase();
        if (!indexISBN.containsKey(isbnKey)) {
            return false;
        }

        Book removedBook = indexISBN.remove(isbnKey);

        // Remove from Trie indexes
        trieTitle.delete(removedBook.getTitle().toLowerCase(), isbnKey);
        trieAuthor.delete(removedBook.getAuthor().toLowerCase(), isbnKey);

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
        if (isbn == null || isbn.trim().isEmpty()) {
            return null;
        }
        return indexISBN.get(isbn.toLowerCase());
    }

     // Time Complexity: O(m) where m = title length
     public List<Book> getBookByTitle(String title) {
         if (title == null || title.trim().isEmpty()) {
             return new ArrayList<>();
         }

         Set<String> isbns = trieTitle.search(title.toLowerCase());
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
        if (prefix == null || prefix.trim().isEmpty()) {
            return new ArrayList<>();
        }
        List<String> isbns = trieTitle.searchByPrefix(prefix.toLowerCase());
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
        if (author == null || author.trim().isEmpty()) {
            return new ArrayList<>();
        }
        Set<String> isbns = trieAuthor.search(author.toLowerCase());
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
        if (prefix == null || prefix.trim().isEmpty()) {
            return new ArrayList<>();
        }
        List<String> isbns = trieAuthor.searchByPrefix(prefix.toLowerCase());
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

        if (category == null || category.trim().isEmpty()) {
            return new ArrayList<>();
        }

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