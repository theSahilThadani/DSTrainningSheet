package Day9.entities;

import java.time.LocalDateTime;
import java.util.UUID;

public class WishlistItem {
    private String wishlistItemId;
    private Product product;
    private long addedAt;
    private int priority;
    private String notes;

    public WishlistItem(Product product) {
        this.wishlistItemId = UUID.randomUUID().toString();
        this.product = product;
        this.addedAt = System.currentTimeMillis();
        this.priority = 0;
        this.notes = "";
    }
    public String getWishlistItemId() { return wishlistItemId; }
    public Product getProduct() { return product; }
    public long getAddedAt() { return addedAt; }
    public int getPriority() { return priority; }
    public String getNotes() { return notes; }

    public void setPriority(int priority) { this.priority = priority; }
    public void setNotes(String notes) { this.notes = notes != null ? notes : ""; }

    @Override
    public String toString() {
        return product.getName() + " (Priority: " + priority + ")";
    }

}
