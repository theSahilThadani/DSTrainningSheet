package Day4;

import java.util.*;


public class BrowserTabsManager {

    static final class Tab{
        private final String title;
        private final String url;
        private final long openedAt;
        private long lastAccessedAt;

        Tab(String title, String url) {
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("Title cannot be empty");
            }

            this.title = title.trim();
            this.url = url != null ? url.trim() : "about:blank";
            this.openedAt = System.currentTimeMillis();
            this.lastAccessedAt = this.openedAt;
        }

        //getters
        public String getTitle() { return title; }
        public String getUrl() { return url; }
        public long getOpenedAt() { return openedAt; }
        public long getLastAccessedAt() { return lastAccessedAt; }

        @Override
        public String toString() {
            return String.format("%s | %s", title, url);
        }

    }

    static class TabManager {
        private final LinkedHashMap<String, Tab> tabs;

        TabManager() {
            tabs = new LinkedHashMap<>();
        }

        public void openTab(String title, String url) {
            String key = normalizeKey(title);

            if (tabs.containsKey(key)) {
                System.out.println("Tab already open: " + title);
                return;
            }

            Tab tab = new Tab(title, url);
            tabs.put(key, tab);
            System.out.println(" Opened: " + tab);
        }

        public Tab closeTab(String title) {
            String key = normalizeKey(title);

            if (!tabs.containsKey(key)) {
                System.out.println("Tab not found: " + title);
                return null;
            }

            Tab tab = tabs.get(key);
            Tab removed = tabs.remove(key);
            System.out.println(" Closed: " + removed);
            return removed;
        }

        public Tab searchTab(String title) {
            String key = normalizeKey(title);
            return tabs.get(key);
        }

        public void displayTabs() {
            if (tabs.isEmpty()) {
                System.out.println("\n No tabs open\n");
                return;
            }

            System.out.println("\n" + "═".repeat(80));
            System.out.println("OPEN TABS ("+ tabs.size()+")");
            System.out.println("═".repeat(80));

            int position = 1;
            for (Tab tab : tabs.values()) {
                System.out.printf("%2d. %s\n", position++, tab);
            }

            System.out.println("═".repeat(80) + "\n");
        }

        public List<Tab> closeAllTabs() {
            List<Tab> closedTabs = new ArrayList<>(tabs.values());
            int count = closedTabs.size();

            tabs.clear();

            System.out.println("✅ Closed all " + count + " tabs");
            return closedTabs;
        }

        private String normalizeKey(String title) {
            return title.trim().toLowerCase();
        }

    }

    static class TabHistory {
        private final Deque<Tab> closedTabs = new ArrayDeque<>();
        private static final int MAX_HISTORY = 50;

        public void addToHistory(Tab tab) {
            if (tab == null) return;

            closedTabs.push(tab);

            if (closedTabs.size() > MAX_HISTORY) {
                closedTabs.removeLast();
            }
        }

        public Tab reopenLastClosed(TabManager manager) {
            if (closedTabs.isEmpty()) {
                System.out.println("No closed tabs to reopen");
                return null;
            }

            Tab tab = closedTabs.pop();
            manager.openTab(tab.getTitle(), tab.getUrl());

            return tab;
        }

        public void displayHistory(int limit) {
            if (closedTabs.isEmpty()) {
                System.out.println("\n No recently closed tabs\n");
                return;
            }

            int displayCount = Math.min(limit, closedTabs.size());

            System.out.println("\n" + "═".repeat(80));
            System.out.println("🕐 RECENTLY CLOSED TABS (Last " + displayCount + ")");
            System.out.println("═".repeat(80));

            int count = 0;
            for (Tab tab : closedTabs) {
                if (count++ >= displayCount) break;
                System.out.printf("%2d. %s\n", count, tab);
            }

            System.out.println("═".repeat(80) + "\n");
        }

        public void addAllToHistory(List<Tab> tabs) {
            if (tabs == null || tabs.isEmpty()) return;

            for (int i = tabs.size() - 1; i >= 0; i--) {
                addToHistory(tabs.get(i));
            }

            System.out.println(tabs.size() + " tabs added to history");
        }

        public int size() { return closedTabs.size(); }
        public void clear() { closedTabs.clear(); }
    }

    private static void displayMenu() {
        System.out.println("\n" + "═".repeat(80));
        System.out.println("🌐 BROWSER TAB MANAGER");
        System.out.println("═".repeat(80));
        System.out.println(" 1.  Open new tab");
        System.out.println(" 2.  Close specific tab");
        System.out.println(" 3.  Display all tabs");
        System.out.println(" 4.  Search tab");
        System.out.println(" 5.  Reopen last closed tab");
        System.out.println(" 6.  View recently closed tabs");
        System.out.println(" 7.  Close all tabs");
        System.out.println(" 8.  Exit");
        System.out.println("═".repeat(80));
    }

    private static int getIntInput(Scanner sc, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = sc.nextLine().trim();

                if (input.isEmpty()) {
                    System.out.println("Input cannot be empty");
                    continue;
                }

                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(" Invalid number. Please enter a valid integer.");
            }
        }
    }

    private static String getStringInput(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();

            if (!input.isEmpty()) {
                return input;
            }

            System.out.println(" Input cannot be empty");
        }
    }


    public static void main(String[] args) {

        TabManager tabManager = new TabManager();
        TabHistory history = new TabHistory();

        System.out.println("\n" + "═".repeat(80));
        System.out.println(" Welcome to Browser Tab Manager ");
        System.out.println("═".repeat(80));

        tabManager.openTab("GitHub", "https://github.com/theSahilThadani");
        tabManager.openTab("Stack Overflow", "https://stackoverflow.com");
        tabManager.openTab("Google", "https://google.com");

        try (Scanner sc = new Scanner(System.in)) {
            boolean running = true;

            while (running) {
                displayMenu();
                int choice = getIntInput(sc, "Enter your choice: ");

                System.out.println();

                try {
                    switch (choice) {
                        case 1: // Open new tab
                            String title = getStringInput(sc, "Enter tab title: ");
                            String url = getStringInput(sc, "Enter URL: ");
                            tabManager.openTab(title, url);
                            break;

                        case 2:
                            String closeTitle = getStringInput(sc, "Enter tab title to close: ");
                            Tab closed = tabManager.closeTab(closeTitle);
                            if (closed != null) {
                                history.addToHistory(closed);
                            }
                            break;

                        case 3:
                            tabManager.displayTabs();
                            break;

                        case 4:
                            String searchTitle = getStringInput(sc, "Enter tab title to search: ");
                            Tab found = tabManager.searchTab(searchTitle);

                            if (found != null) {
                                System.out.println("\n Tab found:");
                                System.out.println("═".repeat(80));
                                System.out.println(found);
                                System.out.println("═".repeat(80) + "\n");
                            } else {
                                System.out.println("Tab not found: " + searchTitle);
                            }
                            break;

                        case 5:
                            history.reopenLastClosed(tabManager);
                            break;

                        case 6:
                            int historyLimit = getIntInput(sc, "How many to display? (1-50): ");
                            history.displayHistory(Math.min(historyLimit, 50));
                            break;

                        case 7:
                            System.out.print(" Close ALL tabs ? (y/n): ");
                            String confirmAll = sc.nextLine().trim().toLowerCase();

                            if (confirmAll.equals("yes") || confirmAll.equals("y")) {
                                List<Tab> closedTabs = tabManager.closeAllTabs();
                                history.addAllToHistory(closedTabs);
                            } else {
                                System.out.println("Operation cancelled");
                            }
                            break;

                        case 8:
                            System.out.println("═".repeat(80));
                            System.out.println(" Thank you for using Browser Tab Manager ");
                            System.out.println(" Goodbye");
                            System.out.println("═".repeat(80) + "\n");
                            running = false;
                            break;

                        default:
                            System.out.println("Invalid choice. Please enter 1-8.");
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

    }
}
