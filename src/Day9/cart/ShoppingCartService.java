package Day9.cart;

import Day9.entities.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class ShoppingCartService {
    private final Map<String, ShoppingCart> carts = new ConcurrentHashMap<>();
    private final Map<String, Product> products = new ConcurrentHashMap<>();
    private final ShoppingCart sharedDiscountCart;  // Admin cart for discounts


    public ShoppingCartService() {
        this.sharedDiscountCart = new ShoppingCart("ADMIN");
    }


    public ShoppingCart getCart(String userId) {
        return carts.computeIfAbsent(userId, uid -> {
            System.out.println("📱 Created new cart for: " + uid);
            return new ShoppingCart(uid);
        });
    }


    public void registerProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        products.put(product.getProductID(), product);
        System.out.println("Registered product: " + product.getName());
    }


    public void registerDiscount(Discount discount) {
        sharedDiscountCart.registerDiscount(discount);
    }


    public void addItemToCart(String userId, String productId, int quantity) {
        ShoppingCart cart = getCart(userId);
        Product product = products.get(productId);

        if (product == null) {
            throw new IllegalArgumentException("Product not found: " + productId);
        }

        cart.addItem(product, quantity);
    }


    public void applyCouponToCart(String userId, String couponCode) {
        ShoppingCart cart = getCart(userId);
        cart.applyCoupon(couponCode);
    }


    public void addToWishlist(String userId, String productId) {
        ShoppingCart cart = getCart(userId);
        Product product = products.get(productId);

        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }

        cart.addToWishlist(product);
    }


    public int getTotalActiveCarts() {
        return carts.size();
    }


    public double getTotalCartValue() {
        return carts.values().stream()
                .mapToDouble(ShoppingCart::calculateGrandTotal)
                .sum();
    }


    public void displayAllCarts() {
        System.out.println("\n ALL SHOPPING CARTS");
        for (ShoppingCart cart : carts.values()) {
            cart.displayCart();
        }
    }
}