package Day8.managers;

import Day8.entities.Book;

import java.util.*;

public class BookManager {
    HashMap<String, Book> IndexISBN;
    HashMap<String, Book> IndexTitle;
    HashMap<String, List<String>> IndexAuthor;
    HashMap<String, List<String>> IndexCategory;

    public boolean addBook(Book book){

        if (IndexISBN.containsKey(book.getISBN())) {
            return false;
        }

        IndexISBN.put(book.getISBN().toLowerCase(), book);
        IndexTitle.put(book.getTitle().toLowerCase(), book);
        IndexAuthor.computeIfAbsent(book.getAuthor().toLowerCase(), k -> new ArrayList<>()).add(book.getISBN().toLowerCase());
        IndexCategory.computeIfAbsent(book.getCategory().toLowerCase(), k -> new ArrayList<>()).add(book.getISBN().toLowerCase());
        return true;
    }

    public boolean removeBook(String ISBN) {
        if(!IndexISBN.containsKey(ISBN)){
            return false;
        }
        Book removedBook = IndexISBN.remove(ISBN);
        IndexISBN.remove(ISBN);
        IndexTitle.remove(removedBook.getTitle());
        List<String> authorList = IndexAuthor.get(removedBook.getAuthor());
        if(authorList != null){
            authorList.remove(removedBook.getISBN());
            if(authorList.isEmpty()){
                IndexAuthor.remove(removedBook.getAuthor());
            }
        }
        List<String> categotyList = IndexCategory.get(removedBook.getCategory());
        if(categotyList != null){
            categotyList.remove(removedBook.getISBN());
            if(categotyList.isEmpty()){
                IndexCategory.remove(removedBook.getCategory());
            }
        }
        return true;
    }

    public Book getBooKByISBN(String ISBN){
        return IndexISBN.get(ISBN);
    }

    public Book getBooKByTitle(String title){
        return IndexTitle.get(title);
    }

    public List<Book> getBooKByAuthor(String author){
        List<String> list = IndexAuthor.get(author.toLowerCase());
        if(list == null){
            return null;
        }
        List<Book> bookList = new ArrayList<>();
        for(String s : list){
            bookList.add(getBooKByISBN(s));
        }
        return bookList;
    }

    public List<Book> getBooKByCategory(String category){
        List<String> list = IndexCategory.get(category.toLowerCase());
        if(list == null){
            return null;
        }
        List<Book> bookList = new ArrayList<>();
        for(String s : list){
            bookList.add(getBooKByISBN(s));
        }
        return bookList;
    }
    public List<Book> getAllBooks(){
        return new ArrayList<>(IndexISBN.values());
    }
}
