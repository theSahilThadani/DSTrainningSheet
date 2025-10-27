package Day9.cart;

import Day9.entities.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Shopping Cart with real-time updates and discount management
 *
 * Data Structure Strategy:
 * - ArrayList for items (O(1) iteration)
 * - HashMap for item ID index (O(1) lookup)
 * - TreeMap for discounts by priority (O(log n) ordered access)
 * - HashMap for discount codes (O(1) lookup)
 * - LinkedHashSet for wishlist (maintains insertion order)
 *
 * Time Complexity:
 * - Add item: O(1)
 * - Remove item: O(1)
 * - Apply discount: O(log n)
 * - Get total: O(n)
 */
public class ShoppingCart {
    private final String cartId;
    private final String userId;

    // Items storage with dual indexing
    private final List<CartItem> items;                          // Primary Sequential
    private final ConcurrentHashMap<String, Integer> itemIndexById;        // Secondary O(1) lookup
    private final ConcurrentHashMap<String, CartItem> itemsByProductId;    // By product ID

    // Discounts with multilevel indexing
    private final List<AppliedDiscount> appliedDiscounts;        // Applied discounts
    private final TreeMap<Integer, Discount> discountsByPriority; // O(log n) priority
    private final ConcurrentHashMap<String, Discount> discountsByCode;     // O(1) code lookup

    // Wishlist
    private final Set<Object> wishlist;
    private final ConcurrentHashMap<String, WishlistItem> wishlistById;

    // Metadata
    private CartStatus status;
    private final long createdAt;
    private long lastUpdatedAt;
    private double shippingCost;
    private double taxRate;
    private String couponCode;

    // Constants
    private static final double MAX_CART_VALUE = 1_000_000;
    private static final int MAX_ITEMS_IN_CART = 500;
    private static final int CART_EXPIRY_HOURS = 24;

    public ShoppingCart(String userId) {
        validateUserId(userId);

        this.cartId = UUID.randomUUID().toString();
        this.userId = userId;
        this.items = Collections.synchronizedList(new ArrayList<>());
        this.itemIndexById = new ConcurrentHashMap<>();
        this.itemsByProductId = new ConcurrentHashMap<>();
        this.appliedDiscounts = Collections.synchronizedList(new ArrayList<>());
        this.discountsByPriority = new TreeMap<>(Collections.reverseOrder());
        this.discountsByCode = new ConcurrentHashMap<>();
        this.wishlist = Collections.synchronizedSet(new LinkedHashSet<>());
        this.wishlistById = new ConcurrentHashMap<>();
        this.status = CartStatus.ACTIVE;
        this.createdAt = System.currentTimeMillis();
        this.lastUpdatedAt = this.createdAt;
        this.shippingCost = 0.0;
        this.taxRate = 0.0;
        this.couponCode = null;
    }


    private void validateUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be empty");
        }
    }


    public synchronized void addItem(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (!product.isInStock(quantity)) {
            throw new IllegalArgumentException("Insufficient stock for: " + product.getName());
        }
        if (items.size() >= MAX_ITEMS_IN_CART) {
            throw new IllegalStateException("Cart is full");
        }

        // Check if product already in cart
        CartItem existing = itemsByProductId.get(product.getProductID());
        if (existing != null) {
            // Update quantity
            int newQuantity = existing.getQuantity() + quantity;
            if (product.isInStock(newQuantity)) {
                existing.setQuantity(newQuantity);
                System.out.println("Updated quantity for " + product.getName());
            } else {
                throw new IllegalArgumentException("Cannot add more: insufficient stock");
            }
        } else {
            // Add new item
            CartItem cartItem = new CartItem(product, quantity);
            items.add(cartItem);
            itemIndexById.put(cartItem.getCartItemId(), items.size() - 1);
            itemsByProductId.put(product.getProductID(), cartItem);
            System.out.println("Added " + quantity + "x " + product.getName());
        }

        this.lastUpdatedAt = System.currentTimeMillis();
    }

    public synchronized void removeItem(String productId) {
        CartItem item = itemsByProductId.get(productId);
        if (item == null) {
            System.out.println("Item not found");
            return;
        }

        // Remove from all indexes
        items.remove(item);
        itemIndexById.remove(item.getCartItemId());
        itemsByProductId.remove(productId);

        // Remove related discounts
        appliedDiscounts.removeIf(ad -> ad.getDiscount().appliesToItem(item));

        System.out.println("Removed " + item.getProduct().getName());
        this.lastUpdatedAt = System.currentTimeMillis();
    }

    public synchronized void updateItemQuantity(String productId, int newQuantity) {
        CartItem item = itemsByProductId.get(productId);
        if (item == null) {
            throw new IllegalArgumentException("Item not found");
        }
        if (newQuantity <= 0) {
            removeItem(productId);
            return;
        }

        Product product = item.getProduct();
        if (!product.isInStock(newQuantity)) {
            throw new IllegalArgumentException("Insufficient stock");
        }

        item.setQuantity(newQuantity);
        System.out.println("Updated quantity to " + newQuantity);
        appliedDiscounts.removeIf(ad -> ad.getDiscount().appliesToItem(item));
        this.lastUpdatedAt = System.currentTimeMillis();
    }

    public CartItem getItem(String productId) {
        return itemsByProductId.get(productId);
    }

    public List<CartItem> getItems() {
        return new ArrayList<>(items);
    }

    public int getItemCount() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }


    public void registerDiscount(Discount discount) {
        if (discount == null) {
            throw new IllegalArgumentException("Discount cannot be null");
        }
        discountsByCode.put(discount.getCode(), discount);
        discountsByPriority.put(discount.getPriority(), discount);
        System.out.println(" Registered discount: " + discount.getCode());
    }


    public synchronized void applyCoupon(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Coupon code cannot be empty");
        }
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        Discount discount = discountsByCode.get(code.toUpperCase());
        if (discount == null) {
            throw new IllegalArgumentException("Invalid coupon code: " + code);
        }

        // Check if already applied
        if (appliedDiscounts.stream()
                .anyMatch(ad -> ad.getDiscount().getCode().equals(code.toUpperCase()))) {
            throw new IllegalArgumentException("Coupon already applied");
        }

        // Check if discount is stackable
        if (!discount.isStackable() && !appliedDiscounts.isEmpty()) {
            throw new IllegalArgumentException("Cannot stack non-stackable coupons");
        }

        // Calculate discount
        double cartTotal = calculateSubtotal();
        if (cartTotal < discount.getMinCartValue()) {
            throw new IllegalArgumentException(
                    String.format("Minimum cart value ₹%.2f required", discount.getMinCartValue()));
        }

        double discountAmount = 0;
        for (CartItem item : items) {
            if (discount.appliesToItem(item)) {
                discountAmount += discount.calculateDiscount(item);
            }
        }

        if (discountAmount > 0) {
            AppliedDiscount applied = new AppliedDiscount(discount, discountAmount);
            appliedDiscounts.add(applied);
            discount.incrementUsage();
            this.couponCode = code.toUpperCase();
            System.out.println("Applied coupon: " + code + " (-₹" + String.format("%.2f", discountAmount) + ")");
        }

        this.lastUpdatedAt = System.currentTimeMillis();
    }

    public synchronized void removeCoupon(String code) {
        boolean removed = appliedDiscounts.removeIf(ad ->
                ad.getDiscount().getCode().equals(code.toUpperCase()));

        if (removed) {
            System.out.println(" Removed coupon: " + code);
            this.couponCode = null;
            this.lastUpdatedAt = System.currentTimeMillis();
        } else {
            System.out.println(" Coupon not applied");
        }
    }


    public List<AppliedDiscount> getAppliedDiscounts() {
        return new ArrayList<>(appliedDiscounts);
    }


    public synchronized void addToWishlist(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }

        WishlistItem existing = wishlistById.get(product.getProductID());
        if (existing != null) {
            System.out.println("Already in wishlist");
            return;
        }

        WishlistItem item = new WishlistItem(product);
        wishlist.add(item);
        wishlistById.put(product.getProductID(), item);
        System.out.println("Added to wishlist: " + product.getName());
    }


    public synchronized void removeFromWishlist(String productId) {
        WishlistItem item = wishlistById.remove(productId);
        if (item != null) {
            wishlist.remove(item);
            System.out.println(" Removed from wishlist");
        }
    }


    public synchronized void moveWishlistToCart(String productId) {
        WishlistItem wishlistItem = wishlistById.get(productId);
        if (wishlistItem == null) {
            throw new IllegalArgumentException("Item not in wishlist");
        }

        addItem(wishlistItem.getProduct(), 1);
        removeFromWishlist(productId);
        System.out.println("Moved to cart from wishlist");
    }


    public List<Object> getWishlist() {
        return new ArrayList<>(wishlist);
    }

    public int getWishlistSize() {
        return wishlist.size();
    }

    public double calculateSubtotal() {
        return items.stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
    }


    public double calculateTotalDiscount() {
        return appliedDiscounts.stream()
                .mapToDouble(AppliedDiscount::getDiscountAmount)
                .sum();
    }

    public double calculateTax() {
        double subtotalAfterDiscount = calculateSubtotal() - calculateTotalDiscount();
        return subtotalAfterDiscount * taxRate;
    }


    public double calculateGrandTotal() {
        double subtotal = calculateSubtotal();
        double discount = calculateTotalDiscount();
        double tax = calculateTax();
        return subtotal - discount + tax + shippingCost;
    }


    public String getCartId() { return cartId; }
    public String getUserId() { return userId; }
    public CartStatus getStatus() { return status; }
    public long getCreatedAt() { return createdAt; }
    public long getLastUpdatedAt() { return lastUpdatedAt; }
    public String getCouponCode() { return couponCode; }

    public void setTaxRate(double taxRate) {
        if (taxRate < 0 || taxRate > 1) {
            throw new IllegalArgumentException("Tax rate must be between 0 and 1");
        }
        this.taxRate = taxRate;
    }

    public void setShippingCost(double shippingCost) {
        if (shippingCost < 0) {
            throw new IllegalArgumentException("Shipping cost cannot be negative");
        }
        this.shippingCost = shippingCost;
    }

    public double getShippingCost() { return shippingCost; }
    public double getTaxRate() { return taxRate; }


    public boolean hasExpired() {
        long ageHours = (System.currentTimeMillis() - lastUpdatedAt) / (1000 * 60 * 60);
        return ageHours > CART_EXPIRY_HOURS;
    }


    public void checkout() {
        if (items.isEmpty()) {
            throw new IllegalStateException("Cannot checkout empty cart");
        }
        this.status = CartStatus.CHECKED_OUT;
        System.out.println(" Checkout complete. Total: ₹" + String.format("%.2f", calculateGrandTotal()));
    }


    public synchronized void clear() {
        items.clear();
        itemIndexById.clear();
        itemsByProductId.clear();
        appliedDiscounts.clear();
        this.status = CartStatus.ACTIVE;
        this.couponCode = null;
        System.out.println("Cart cleared");
    }


    public void displayCart() {
        System.out.println("\n" + "═".repeat(80));
        System.out.println("🛒 SHOPPING CART - " + userId);
        System.out.println("═".repeat(80));

        if (items.isEmpty()) {
            System.out.println("Cart is empty");
        } else {
            System.out.println("\n📦 ITEMS:");
            int itemNo = 1;
            for (CartItem item : items) {
                System.out.printf("  %d. %s\n", itemNo++, item);
            }
        }

        System.out.println("\n💰 PRICING:");
        System.out.printf("  Subtotal:        ₹%.2f\n", calculateSubtotal());

        if (!appliedDiscounts.isEmpty()) {
            System.out.println("\n  DISCOUNTS:");
            for (AppliedDiscount ad : appliedDiscounts) {
                System.out.printf("  - %s\n", ad);
            }
            System.out.printf("  Total Discount:  -₹%.2f\n", calculateTotalDiscount());
        }

        System.out.printf("  Tax (%.1f%%):     ₹%.2f\n", taxRate * 100, calculateTax());
        System.out.printf("  Shipping:        ₹%.2f\n", shippingCost);
        System.out.println("  " + "─".repeat(35));
        System.out.printf("  GRAND TOTAL:     ₹%.2f\n", calculateGrandTotal());

        if (!wishlist.isEmpty()) {
            System.out.println("\n❤WISHLIST (" + wishlist.size() + " items):");
            int no = 1;
            for (Object item : wishlist) {
                System.out.printf("  %d. %s\n", no++, item);
            }
        }

        System.out.println("═".repeat(80) + "\n");
    }

    @Override
    public String toString() {
        return String.format("Cart{userId='%s', items=%d, total=₹%.2f, status=%s}",
                userId, items.size(), calculateGrandTotal(), status);
    }
}