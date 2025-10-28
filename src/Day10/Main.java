package Day10;

import Day10.entities.URLData;
import Day10.repositories.URLRepository;
import Day10.services.URLShortenService;

import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
public class Main {

    public static void main(String[] args) {
        URLRepository repository = new URLRepository();
        URLShortenService service = new URLShortenService(repository);

        try (Scanner sc = new Scanner(System.in)) {
            boolean running = true;
            System.out.println("\n" + "═".repeat(80));
            System.out.println("Day10 URL Shortener");
            System.out.println("═".repeat(80));

            while (running) {
                printMenu();
                int choice = readInt(sc, "Choose: ");
                try {
                    switch (choice) {
                        case 1 -> handleShorten(sc, service);
                        case 2 -> handleShortenCustom(sc, service);
                        case 3 -> handleResolve(sc, service);
                        case 4 -> handleUrlDetails(sc, service);
                        case 5 -> handleUserUrls(sc, service);
                        case 6 -> handleTopClicked(sc, service);
                        case 7 -> handleDelete(sc, service);
                        case 8 -> handleSimulateClicks(sc, service);
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
        } finally {
            service.shutdown();
        }
    }

    private static void printMenu() {
        System.out.println("\n--- MENU ---");
        System.out.println("1. Shorten URL (random code)");
        System.out.println("2. Shorten URL with custom code");
        System.out.println("3. Resolve short code (get original URL + increment clicks)");
        System.out.println("4. Show URL details by code");
        System.out.println("5. List URLs for a user");
        System.out.println("6. Show top N most-clicked URLs");
        System.out.println("7. Delete a short code (with user check)");
        System.out.println("8. Simulate clicks on a code");
        System.out.println("0. Exit");
    }

    private static void handleShorten(Scanner sc, URLShortenService service) {
        String url = readLine(sc, "Enter long URL: ");
        String user = readLine(sc, "User ID: ");
        String code = service.shortenURL(url, user);
        System.out.println("Short code: " + code);
    }

    private static void handleShortenCustom(Scanner sc, URLShortenService service) {
        String url = readLine(sc, "Enter long URL: ");
        String custom = readLine(sc, "Custom code (alias): ");
        String user = readLine(sc, "User ID: ");
        String code = service.shortenURLWithCustomCode(url, custom, user);
        System.out.println("Custom short code: " + code);
    }

    private static void handleResolve(Scanner sc, URLShortenService service) {
        String code = readLine(sc, "Enter short code: ");
        String original = service.getOriginalURL(code);
        System.out.println("🔗 Original URL: " + original);
    }

    private static void handleUrlDetails(Scanner sc, URLShortenService service) {
        String code = readLine(sc, "Enter short code: ");
        URLData data = service.getURLDetails(code);
        try {
            var method = URLData.class.getMethod("toDetailedString");
            Object s = method.invoke(data);
            System.out.println(s.toString());
        } catch (Exception reflectFallback) {
            System.out.println("Code: " + getSafe(data, URLData::getShortCode, "<unknown>"));
            System.out.println("URL: " + getSafe(data, URLData::getOriginalURL, "<unknown>"));
            System.out.println("Clicks: " + getSafeLong(data, URLData::getClicks, -1));
            System.out.println("CreatedAt(ms): " + getSafeLong(data, URLData::getCreatedAt, -1));
        }
    }

    private static void handleUserUrls(Scanner sc, URLShortenService service) {
        String user = readLine(sc, "User ID: ");
        List<URLData> list = service.getUserURLs(user);
        if (list == null || list.isEmpty()) {
            System.out.println("No URLs for user.");
            return;
        }
        list.sort(Comparator.comparingLong(URLData::getCreatedAt).reversed());
        System.out.println("\nURLs for user '" + user + "' (" + list.size() + "):");
        int i = 1;
        for (URLData u : list) {
            System.out.printf("  %d. %s → %s (clicks=%d)%n",
                    i++, getSafe(u, URLData::getShortCode, "?"),
                    getSafe(u, URLData::getOriginalURL, "?"),
                    getSafeLong(u, URLData::getClicks, 0));
        }
    }

    private static void handleTopClicked(Scanner sc, URLShortenService service) {
        int n = readInt(sc, "Show top N: ");
        List<URLData> top = service.getTopClickedURLs(n);
        if (top == null || top.isEmpty()) {
            System.out.println("No data.");
            return;
        }
        System.out.println("\nTop " + n + " Most Clicked URLs:");
        int rank = 1;
        for (URLData u : top) {
            System.out.printf("  %d. %s - %d clicks (%s)%n",
                    rank++, getSafe(u, URLData::getShortCode, "?"),
                    getSafeLong(u, URLData::getClicks, 0),
                    getSafe(u, URLData::getOriginalURL, "?"));
        }
    }

    private static void handleDelete(Scanner sc, URLShortenService service) {
        String code = readLine(sc, "Short code to delete: ");
        String user = readLine(sc, "User ID: ");
        boolean ok = service.deleteURL(code, user);
        System.out.println(ok ? "Deleted" : " Not found or not authorized");
    }

    private static void handleSimulateClicks(Scanner sc, URLShortenService service) {
        String code = readLine(sc, "Short code: ");
        int times = readInt(sc, "Number of clicks to simulate: ");
        for (int i = 0; i < times; i++) {
            try {
                service.getOriginalURL(code);
            } catch (Exception e) {
                System.out.println("Stopped: " + e.getMessage());
                break;
            }
        }
        URLData data = service.getURLDetails(code);
        System.out.println("Total clicks now: " + (data != null ? getSafeLong(data, URLData::getClicks, 0) : 0));
    }

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

    private static <T> String getSafe(T obj, java.util.function.Function<T, String> f, String def) {
        try {
            return obj == null ? def : f.apply(obj);
        } catch (Exception e) {
            return def;
        }
    }

    private static <T> long getSafeLong(T obj, java.util.function.ToLongFunction<T> f, long def) {
        try {
            return obj == null ? def : f.applyAsLong(obj);
        } catch (Exception e) {
            return def;
        }
    }
}