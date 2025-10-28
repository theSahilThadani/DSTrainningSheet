package Day10.services;

import Day10.entities.URLData;
import Day10.repositories.URLRepository;
import Day10.utils.CodeGenerator;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class URLShortenService {
    private final URLRepository repository;
    private final ScheduledExecutorService scheduler;
    private static final int MAX_RETRIES = 5;
    private long totalURLsCreated = 0;
    private long totalClicks = 0;

    public URLShortenService(URLRepository repository) {
        this.repository = repository;
        this.scheduler = Executors.newScheduledThreadPool(1, r -> {
            Thread t = new Thread(r, "URLCleanup-Thread");
            t.setDaemon(true);
            return t;
        });
        startCleanupTask();
    }

    public synchronized String shortenURL(String originalURL, String userId) {
        if (originalURL == null || originalURL.isEmpty()) {
            throw new IllegalArgumentException("URL cannot be empty");
        }

        // Check if already shortened
        String existingCode = repository.findShortCodeByURL(originalURL);
        if (existingCode != null) {
            System.out.println("URL already shortened: " + existingCode);
            return existingCode;
        }

        // Generate new short code
        String shortCode = generateUniqueCode();
        URLData urlData = new URLData(shortCode, originalURL, userId);

        repository.save(urlData);
        totalURLsCreated++;

        System.out.println("URL shortened: " + shortCode + " → " + originalURL);

        return shortCode;
    }

    private void startCleanupTask() {
        scheduler.scheduleAtFixedRate(
                this::cleanExpiredURLs,
                1, 24, TimeUnit.HOURS
        );
        System.out.println("Started background cleanup task");
    }

     // Shorten URL with custom short code - O(1)
    public String shortenURLWithCustomCode(String originalURL, String customCode, String userId) {
        if (customCode == null || customCode.isEmpty()) {
            throw new IllegalArgumentException("Custom code cannot be empty");
        }

        if (repository.exists(customCode)) {
            throw new IllegalArgumentException("Short code already taken: " + customCode);
        }

        try {
            URLData urlData = new URLData(customCode, originalURL, userId);
            repository.save(urlData);
            totalURLsCreated++;
            System.out.println("Custom short URL: " + customCode);
            return customCode;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid custom code: " + e.getMessage());
        }
    }


     //Get original URL - O(1)
     //Tracks clicks and handles expired URLs
    public String getOriginalURL(String shortCode) {
        URLData urlData = repository.findByShortCode(shortCode);

        if (urlData == null) {
            throw new IllegalArgumentException("Short code not found: " + shortCode);
        }

        if (urlData.isExpired()) {
            repository.delete(shortCode);
            throw new IllegalArgumentException("Short URL has expired");
        }

        // Track click
        urlData.incrementClicks();
        totalClicks++;

        return urlData.getOriginalURL();
    }

    //get clicks
    public int getTotalClicks(String shortCode){
        URLData urlData = repository.findByShortCode(shortCode);
        if (urlData == null) {
            throw new IllegalArgumentException("Short code not found: " + shortCode);
        }
        return urlData.getClicks();
    }

     // Delete URL - O(1)
    public boolean deleteURL(String shortCode, String userId) {
        URLData urlData = repository.findByShortCode(shortCode);

        if (urlData == null) {
            throw new IllegalArgumentException("Short code not found");
        }

        // Check authorization
        if (!urlData.getUserId().equals(userId) && !userId.equals("admin")) {
            throw new IllegalArgumentException("Unauthorized to delete this URL");
        }

        repository.delete(shortCode);
        System.out.println("URL deleted: " + shortCode);
        return true;
    }


     //Get all URLs for user - O(k)
    public List<URLData> getUserURLs(String userId) {
        return repository.findByUserId(userId);
    }

     // Get URL details  O(1)
    public URLData getURLDetails(String shortCode) {
        URLData urlData = repository.findByShortCode(shortCode);
        if (urlData == null) {
            throw new IllegalArgumentException("Short code not found");
        }
        if (urlData.isExpired()) {
            repository.delete(shortCode);
            throw new IllegalArgumentException("Short URL has expired");
        }
        return urlData;
    }

     // Get top clicked URLs  O(n log k)
    public List<URLData> getTopClickedURLs(int limit) {
        return repository.getTopClickedURLs(limit);
    }

     // Clean expired URLs
    public int cleanExpiredURLs() {
        return repository.cleanExpired();
    }

     // Generate unique short code with retry logic - O(1) average
    private String generateUniqueCode() {
        for (int i = 0; i < MAX_RETRIES; i++) {
            String code = CodeGenerator.generateRandomCode(6);
            if (!repository.exists(code)) {
                return code;
            }
        }

        // Fallback and use hybrid code with timestamp
        return CodeGenerator.generateHybridCode();
    }
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        System.out.println("Service shutdown complete");
    }
}