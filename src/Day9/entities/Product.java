package Day9.entities;

public class Product {
    private String productId;
    private String name;
    private double price;
    private int stockQuantity;
    private String category;

    public Product(String productId, String name, double price, int stockQuantity, String category) {
        validateProduct(productId, name, price, stockQuantity);
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
    }

    public void validateProduct(String id, String name, double price, int stock) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
    }

    public String getProductId() { return productId; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getStockQuantity() { return stockQuantity; }
    public String getCategory() { return category; }

    public boolean isInStock(int requiredQuantity) {
        return stockQuantity >= requiredQuantity;
    }
    @Override
    public String toString() {
        return String.format("%s (₹%.2f) - %d in stock", name, price, stockQuantity);
    }
}
