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

        public Bookbuilder setTitle(String Title){
            this.title = Title;
            return this;
        }
        public Bookbuilder setAuthor(String Author){
            this.author = Author;
            return this;
        }
        public Bookbuilder setISBN(String ISBN){
            this.isbn = ISBN;
            return this;
        }
        public Bookbuilder setDescription(String Description){
            this.description = Description;
            return this;
        }
        public Bookbuilder setCategory(String Category){
            this.category = Category;
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
