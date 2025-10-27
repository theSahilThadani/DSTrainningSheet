package Day9.cart;

import Day9.entities.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ShoppingCartService {
    private final Map<String, ShoppingCart> carts = new ConcurrentHashMap<>();
    private final Map<String, Product> products = new ConcurrentHashMap<>();

    // Global discount registry so new carts also know all coupons
    private final Map<String, Discount> discounts = new ConcurrentHashMap<>();

    public ShoppingCartService() {}

    public ShoppingCart getCart(String userId) {
        return carts.computeIfAbsent(userId, uid -> {
            System.out.println("📱 Created new cart for: " + uid);
            ShoppingCart cart = new ShoppingCart(uid);
            // preload all known discounts into the new cart
            for (Discount d : discounts.values()) {
                cart.registerDiscount(d);
            }
            return cart;
        });
    }

    public void registerProduct(Product product) {
        if (product == null) throw new IllegalArgumentException("Product cannot be null");
        products.put(product.getProductId(), product);
        System.out.println("Registered product: " + product.getName());
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }

    public void registerDiscount(Discount discount) {
        if (discount == null) throw new IllegalArgumentException("Discount cannot be null");
        // store globally
        discounts.put(discount.getCode(), discount);
        // register in all existing carts
        for (ShoppingCart cart : carts.values()) {
            cart.registerDiscount(discount);
        }
    }

    public void addItemToCart(String userId, String productId, int quantity) {
        ShoppingCart cart = getCart(userId);
        Product product = products.get(productId);
        if (product == null) throw new IllegalArgumentException("Product not found: " + productId);
        cart.addItem(product, quantity);
    }

    public void applyCouponToCart(String userId, String couponCode) {
        ShoppingCart cart = getCart(userId);
        // ensure coupon exists globally and is also registered in this cart
        Discount d = discounts.get(couponCode.toUpperCase());
        if (d == null) throw new IllegalArgumentException("Unknown coupon: " + couponCode);
        cart.registerDiscount(d);
        cart.applyCoupon(couponCode);
    }

    public void addToWishlist(String userId, String productId) {
        ShoppingCart cart = getCart(userId);
        Product product = products.get(productId);
        if (product == null) throw new IllegalArgumentException("Product not found");
        cart.addToWishlist(product);
    }

    public void displayAllCarts() {
        System.out.println("\n ALL SHOPPING CARTS");
        for (ShoppingCart cart : carts.values()) {
            cart.displayCart();
        }
    }
}