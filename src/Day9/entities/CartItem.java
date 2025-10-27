package Day9.entities;

import java.util.UUID;

public class CartItem {
    private String cartItemId;
    private Product product;
    private int quantity;
    private long addedAt;
    private long lastUpdatedAt;

    public CartItem(Product product, int quantity) {
        validateQuantity(quantity);
        this.cartItemId = UUID.randomUUID().toString();
        this.product = product;
        this.quantity = quantity;
        this.addedAt = System.currentTimeMillis();
        this.lastUpdatedAt = this.addedAt;
    }

    private void validateQuantity(int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (qty > 1000) {
            throw new IllegalArgumentException("Quantity cannot exceed 1000");
        }
    }

    public String getCartItemId() { return cartItemId; }
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public long getAddedAt() { return addedAt; }
    public long getLastUpdatedAt() { return lastUpdatedAt; }

    public synchronized void setQuantity(int newQuantity) {
        validateQuantity(newQuantity);
        this.quantity = newQuantity;
        this.lastUpdatedAt = System.currentTimeMillis();
    }

    public double getTotalPrice() {
        return product.getPrice() * quantity;
    }

    @Override
    public String toString() {
        return String.format("%s x%d = ₹%.2f", product.getName(), quantity, getTotalPrice());
    }
}