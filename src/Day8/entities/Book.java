package Day8.entities;

public class Book {
    private String title;
    private String author;
    private String isbn;
    private String description;
    private String category;

    private Book(Bookbuilder b){
        this.title =  b.title;
        this.author = b.author;
        this.isbn = b.isbn;
        this.description = b.description;
        this.category = b.category;
    }

    public static class Bookbuilder{
        private String title;
        private String author;
        private String isbn;
        private String description;
        private String category;

        public Bookbuilder setTitle(String title){
            this.title = title;
            return this;
        }
        public Bookbuilder setAuthor(String author){
            this.author = author;
            return this;
        }
        public Bookbuilder setISBN(String isbn){
            this.isbn = isbn;
            return this;
        }
        public Bookbuilder setDescription(String description){
            this.description = description;
            return this;
        }
        public Bookbuilder setCategory(String category){
            this.category = category;
            return this;
        }
        public Book build(){
            return new Book(this);
        }
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getISBN() {
        return isbn;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }
}
