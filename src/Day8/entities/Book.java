package Day8.entities;

public class Book {
    private String Title;
    private String Author;
    private String ISBN;
    private String Description;
    private String Category;

    private Book(Bookbuilder b){
        this.Title =  b.Title;
        this.Author = b.Author;
        this.ISBN = b.ISBN;
        this.Description = b.Description;
        this.Category = b.Category;
    }

    public static class Bookbuilder{
        private String Title;
        private String Author;
        private String ISBN;
        private String Description;
        private String Category;

        public Bookbuilder setTitle(String Title){
            this.Title = Title;
            return this;
        }
        public Bookbuilder setAuthor(String Author){
            this.Author = Author;
            return this;
        }
        public Bookbuilder setISBN(String ISBN){
            this.ISBN = ISBN;
            return this;
        }
        public Bookbuilder setDescription(String Description){
            this.Description = Description;
            return this;
        }
        public Bookbuilder setCategory(String Category){
            this.Category = Category;
            return this;
        }
        public Book bookbuilder(){
            return new Book(this);
        }
    }

    public String getTitle() {
        return Title;
    }

    public String getAuthor() {
        return Author;
    }

    public String getISBN() {
        return ISBN;
    }

    public String getDescription() {
        return Description;
    }

    public String getCategory() {
        return Category;
    }
}
