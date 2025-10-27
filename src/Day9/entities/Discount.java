package Day9.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Discount {
    private String discountID;
    private String code;
    private DiscountType discountType;
    private double value;
    private int priority;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private int maxUses;
    private double minCartValue;
    private Set<String> applicableCategories;
    private Set<String> applicableProducts;
    private int usageCount;
    private boolean stackable;

    public Discount(String code, DiscountType type, double value, int priority, LocalDateTime validFrom, LocalDateTime validUntil, int maxUses, double minCartValue) {
     validateDiscount(code, type, value, maxUses, minCartValue);
        this.discountID = UUID.randomUUID().toString();
        this.code = code.toUpperCase();
        this.discountType = type;
        this.value = value;
        this.priority = priority;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.maxUses = maxUses;
        this.minCartValue = minCartValue;
        this.applicableCategories = ConcurrentHashMap.newKeySet();
        this.applicableProducts = ConcurrentHashMap.newKeySet();
        this.usageCount = 0;
        this.stackable = true;
    }

    private void validateDiscount(String code, DiscountType type, double value, int maxUses, double minCart) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Discount code cannot be empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Discount type cannot be null");
        }
        if (value < type.getMinValue() || value > type.getMaxValue()) {
            throw new IllegalArgumentException("Invalid discount value for type: " + type);
        }
        if (maxUses < 0) {
            throw new IllegalArgumentException("Max uses cannot be negative");
        }
        if (minCart < 0) {
            throw new IllegalArgumentException("Min cart value cannot be negative");
        }
    }

    public String getDiscountId() { return discountID; }
    public String getCode() { return code; }
    public DiscountType getType() { return discountType; }
    public double getValue() { return value; }
    public int getPriority() { return priority; }
    public int getMaxUses() { return maxUses; }
    public int getUsageCount() { return usageCount; }
    public double getMinCartValue() { return minCartValue; }
    public LocalDateTime getValidFrom() { return validFrom; }
    public LocalDateTime getValidUntil() { return validUntil; }
    public Set<String> getApplicableCategories() { return new HashSet<>(applicableCategories); }
    public Set<String> getApplicableProducts() { return new HashSet<>(applicableProducts); }
    public boolean isStackable() { return stackable; }

    public void setStackable(boolean stackable) { this.stackable = stackable; }

    public void addApplicableCategory(String category) { applicableCategories.add(category.toLowerCase()); }

    public boolean isValid(){
        LocalDateTime now = LocalDateTime.now();

        // Check date range
        if (now.isBefore(validFrom) || now.isAfter(validUntil)) {
            return false;
        }

        // Check usage limit
        if (maxUses > 0 && usageCount >= maxUses) {
            return false;
        }

        return true;
    }

    public boolean appliesToItem(CartItem item) {
        if (!isValid()) {
            return false;
        }

        String productId = item.getProduct().getProductID();
        String category = item.getProduct().getCategory();

        // If specific products are listed, must match
        if (!applicableProducts.isEmpty() && !applicableProducts.contains(productId)) {
            return false;
        }

        // If categories are listed, must match
        if (!applicableCategories.isEmpty() && !applicableCategories.contains(category.toLowerCase())) {
            return false;
        }

        return true;
    }


    public double calculateDiscount(CartItem item) {
        if (!appliesToItem(item)) {
            return 0.0;
        }

        double itemTotal = item.getTotalPrice();

        switch (discountType) {
            case PERCENTAGE:
                return itemTotal * (value / 100.0);
            case FIXED_AMOUNT:
                return Math.min(value * item.getQuantity(), itemTotal);
            case BUY_X_GET_Y:
                return (item.getQuantity() / (int)value) * item.getProduct().getPrice();
            case FREE_SHIPPING:
                return 0.0;
            default:
                return 0.0;
        }
    }


    public synchronized void incrementUsage() {
        this.usageCount++;
    }


    public boolean canApply(CartItem item, double cartTotal) {
        return isValid() && appliesToItem(item) && cartTotal >= minCartValue;
    }

    @Override
    public String toString() {
        return String.format("%s (%s %.2f) - Priority: %d, Valid: %s to %s",
                code, discountType, value, priority, validFrom, validUntil);
    }
}
