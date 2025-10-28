package Day10.entities;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public final class URLData {
    private final String shortCode;
    private final String originalURL;
    private final String userId;
    private volatile long createdAt;
    private volatile long expiresAt;
    private final AtomicInteger clicks;

    private static final long DEFAULT_TTL_MS = 30L * 24 * 60 * 60 * 1000; //expiry in 30 days
    private static final String URL_PATTERN = "^https?://.*|^www\\..*";
    private static final String CODE_PATTERN = "^[a-zA-Z0-9_-]{4,20}$";


    public URLData(String shortCode, String originalURL, String userId) {
        this(shortCode, originalURL, userId, System.currentTimeMillis() + DEFAULT_TTL_MS);
    }

     // Create URL mapping with custom expiration

    public URLData(String shortCode, String originalURL, String userId, long expiresAt) {
        validateInput(shortCode, originalURL);

        this.shortCode = shortCode;
        this.originalURL = originalURL;
        this.userId = userId != null ? userId : "UserDemo";
        this.createdAt = System.currentTimeMillis();
        this.expiresAt = expiresAt;
        this.clicks = new AtomicInteger(0);;
    }


     // Validate input parameters

    private void validateInput(String shortCode, String originalURL) {
        if (shortCode == null || shortCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Short code cannot be empty");
        }
        if (originalURL == null || originalURL.trim().isEmpty()) {
            throw new IllegalArgumentException("Original URL cannot be empty");
        }
        if (!originalURL.matches(URL_PATTERN)) {
            throw new IllegalArgumentException("Invalid URL format: " + originalURL);
        }
        if (!shortCode.matches(CODE_PATTERN)) {
            throw new IllegalArgumentException("Short code must be 4-20 alphanumeric characters, dash, or underscore");
        }
    }


    public String getShortCode() { return shortCode; }
    public String getOriginalURL() { return originalURL; }
    public String getUserId() { return userId; }
    public long getCreatedAt() { return createdAt; }
    public long getExpiresAt() { return expiresAt; }
    public int getClicks() { return clicks.get(); }

     // Check if URL has expired - O(1)
    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }

     // Get remaining TTL in milliseconds  O(1)
    public long getRemainingTTL() {
        long remaining = expiresAt - System.currentTimeMillis();
        return Math.max(0, remaining);
    }

     // Increment click count (atomic operation) - O(1)
    public synchronized void incrementClicks() {
        clicks.incrementAndGet();;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof URLData)) return false;
        URLData urlData = (URLData) o;
        return shortCode.equals(urlData.shortCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shortCode);
    }

    @Override
    public String toString() {
        return String.format("URLData{code='%s', url='%s', clicks=%d, expires=%s}",
                shortCode, originalURL, clicks, isExpired() ? "EXPIRED" : "ACTIVE");
    }
}