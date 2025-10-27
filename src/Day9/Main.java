package Day9;

import Day9.cart.ShoppingCart;
import Day9.cart.ShoppingCartService;
import Day9.entities.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final ShoppingCartService service = new ShoppingCartService();

    public static void main(String[] args) {
        seedSampleData();
        try (Scanner sc = new Scanner(System.in)) {
            boolean running = true;
            System.out.println("\n" + "═".repeat(80));
            System.out.println("Shopping Cart - Console");
            System.out.println("═".repeat(80));

            while (running) {
                printMenu();
                int choice = readInt(sc, "Choose: ");
                try {
                    switch (choice) {
                        case 1 -> handleRegisterProduct(sc);
                        case 2 -> handleListProducts();
                        case 3 -> handleAddItem(sc);
                        case 4 -> handleUpdateQty(sc);
                        case 5 -> handleRemoveItem(sc);
                        case 6 -> handleApplyCoupon(sc);
                        case 7 -> handleRemoveCoupon(sc);
                        case 8 -> handleSetTax(sc);
                        case 9 -> handleSetShipping(sc);
                        case 10 -> handleAddToWishlist(sc);
                        case 11 -> handleRemoveFromWishlist(sc);
                        case 12 -> handleMoveWishlistToCart(sc);
                        case 13 -> handleDisplayCart(sc);
                        case 14 -> handleCheckout(sc);
                        case 15 -> handleRegisterDiscount(sc);
                        case 16 -> service.displayAllCarts();
                        case 0 -> {
                            running = false;
                            System.out.println("Goodbye!");
                        }
                        default -> System.out.println("Invalid choice");
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n--- MENU ---");
        System.out.println("1.  Register product -- admin");
        System.out.println("2.  List products");
        System.out.println("3.  Add item to cart");
        System.out.println("4.  Update item quantity");
        System.out.println("5.  Remove item from cart");
        System.out.println("6.  Apply coupon");
        System.out.println("7.  Remove coupon");
        System.out.println("8.  Set tax rate");
        System.out.println("9.  Set shipping cost");
        System.out.println("10. Add to wishlist");
        System.out.println("11. Remove from wishlist");
        System.out.println("12. Move wishlist item to cart");
        System.out.println("13. Display my cart");
        System.out.println("14. Checkout");
        System.out.println("15. Register discount -- admin");
        System.out.println("16. Display all carts -- admin");
        System.out.println("0.  Exit");
    }

    private static void handleRegisterProduct(Scanner sc) {
        String userMsg = "Registering a product";
        System.out.println("\n" + userMsg);

        String id = readLine(sc, "Product ID: ");
        String name = readLine(sc, "Name: ");
        double price = readDouble(sc, "Price: ");
        int stock = readInt(sc, "Stock quantity: ");
        String category = readLine(sc, "Category: ");

        Product p = new Product(id, name, price, stock, category);
        service.registerProduct(p);
        System.out.println("Registered: " + p);
    }

    private static void handleListProducts() {
        List<Product> all = service.getAllProducts();
        System.out.println("\nProducts (" + all.size() + "):");
        for (Product p : all) {
            System.out.println(" - " + p.getProductID() + " :: " + p);
        }
    }

    private static void handleAddItem(Scanner sc) {
        String userId = readLine(sc, "User ID: ");
        String pid = readLine(sc, "Product ID: ");
        int qty = readInt(sc, "Quantity: ");
        service.addItemToCart(userId, pid, qty);
        System.out.println("Item added.");
    }

    private static void handleUpdateQty(Scanner sc) {
        String userId = readLine(sc, "User ID: ");
        String pid = readLine(sc, "Product ID: ");
        int qty = readInt(sc, "New Quantity: ");
        ShoppingCart cart = service.getCart(userId);
        cart.updateItemQuantity(pid, qty);
        System.out.println("Quantity updated.");
    }

    private static void handleRemoveItem(Scanner sc) {
        String userId = readLine(sc, "User ID: ");
        String pid = readLine(sc, "Product ID: ");
        ShoppingCart cart = service.getCart(userId);
        cart.removeItem(pid);
    }

    private static void handleApplyCoupon(Scanner sc) {
        String userId = readLine(sc, "User ID: ");
        String code = readLine(sc, "Coupon code: ");
        service.applyCouponToCart(userId, code);
    }

    private static void handleRemoveCoupon(Scanner sc) {
        String userId = readLine(sc, "User ID: ");
        String code = readLine(sc, "Coupon code to remove: ");
        ShoppingCart cart = service.getCart(userId);
        cart.removeCoupon(code);
    }

    private static void handleSetTax(Scanner sc) {
        String userId = readLine(sc, "User ID: ");
        double taxRate = readDouble(sc, "Tax rate (0.00 to 1.00): ");
        ShoppingCart cart = service.getCart(userId);
        cart.setTaxRate(taxRate);
        System.out.println("Tax updated.");
    }

    private static void handleSetShipping(Scanner sc) {
        String userId = readLine(sc, "User ID: ");
        double shipping = readDouble(sc, "Shipping cost: ");
        ShoppingCart cart = service.getCart(userId);
        cart.setShippingCost(shipping);
        System.out.println("Shipping updated.");
    }

    private static void handleAddToWishlist(Scanner sc) {
        String userId = readLine(sc, "User ID: ");
        String pid = readLine(sc, "Product ID: ");
        service.addToWishlist(userId, pid);
    }

    private static void handleRemoveFromWishlist(Scanner sc) {
        String userId = readLine(sc, "User ID: ");
        String pid = readLine(sc, "Product ID: ");
        ShoppingCart cart = service.getCart(userId);
        cart.removeFromWishlist(pid);
    }

    private static void handleMoveWishlistToCart(Scanner sc) {
        String userId = readLine(sc, "User ID: ");
        String pid = readLine(sc, "Product ID: ");
        ShoppingCart cart = service.getCart(userId);
        cart.moveWishlistToCart(pid);
    }

    private static void handleDisplayCart(Scanner sc) {
        String userId = readLine(sc, "User ID: ");
        ShoppingCart cart = service.getCart(userId);
        cart.displayCart();
    }

    private static void handleCheckout(Scanner sc) {
        String userId = readLine(sc, "User ID: ");
        ShoppingCart cart = service.getCart(userId);
        cart.checkout();
    }

    private static void handleRegisterDiscount(Scanner sc) {
        System.out.println("\nRegistering a discount:");
        String code = readLine(sc, "Code: ").toUpperCase();
        System.out.println("Type: 1=PERCENTAGE 2=FIXED_AMOUNT 3=BUY_X_GET_Y 4=FREE_SHIPPING");
        int t = readInt(sc, "Choose type: ");
        DiscountType type = switch (t) {
            case 1 -> DiscountType.PERCENTAGE;
            case 2 -> DiscountType.FIXED_AMOUNT;
            case 3 -> DiscountType.BUY_X_GET_Y;
            case 4 -> DiscountType.FREE_SHIPPING;
            default -> throw new IllegalArgumentException("Invalid type");
        };
        double value = readDouble(sc, "Value (percent/amount/x for BUY_X_GET_Y): ");
        int priority = readInt(sc, "Priority (higher = applied earlier): ");
        double minCart = readDouble(sc, "Minimum cart value to apply (₹): ");
        int validDays = readInt(sc, "Valid for N days from today: ");

        LocalDateTime now = LocalDateTime.now();
        Discount d = new Discount(code, type, value, priority, now.minusDays(1), now.plusDays(validDays), 0, minCart);

        String stack = readLine(sc, "Stackable? (y/n): ").trim().toLowerCase();
        d.setStackable(stack.equals("y") || stack.equals("yes"));

        String limitToCategory = readLine(sc, "Limit to a category? (blank for none): ").trim();
        if (!limitToCategory.isEmpty()) {
            d.addApplicableCategory(limitToCategory);
        }

        service.registerDiscount(d);
        System.out.println("Discount registered: " + d);
    }

    // Input helpers
    private static String readLine(Scanner sc, String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private static int readInt(Scanner sc, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number, try again.");
            }
        }
    }

    private static double readDouble(Scanner sc, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number, try again.");
            }
        }
    }

    // Optional seeding to demo quickly
    private static void seedSampleData() {
        service.registerProduct(new Product("P100", "Phone", 14999.0, 50, "electronics"));
        service.registerProduct(new Product("P200", "Headphones", 1999.0, 200, "electronics"));
        service.registerProduct(new Product("P300", "Shoes", 2999.0, 120, "fashion"));

        LocalDateTime now = LocalDateTime.now();
        Discount d1 = new Discount("WELCOME10", DiscountType.PERCENTAGE, 10.0, 10, now.minusDays(1), now.plusDays(30), 0, 0.0);
        d1.setStackable(true);
        service.registerDiscount(d1);
    }
}