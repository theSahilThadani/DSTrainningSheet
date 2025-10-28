package Day10.repositories;

import Day10.entities.URLData;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class URLRepository {
    private final Map<String, URLData> urlByShortCode = new ConcurrentHashMap<>();
    private final Map<String, String> shortCodeByURL = new ConcurrentHashMap<>();
    private final TreeMap<Long, Set<String>> expiryIndex = new TreeMap<>();

    //this is for finding shortcode by userID
    private final Map<String, Set<String>> codeByUser = new ConcurrentHashMap<>();
    private final ReadWriteLock expiryLock = new ReentrantReadWriteLock();


    public void save(URLData urlData) {
        //this will save url data into this repo
        if(urlData == null){
            throw new IllegalArgumentException("URLData cannot be null");
        }
        urlByShortCode.put(urlData.getShortCode(), urlData);
        shortCodeByURL.put(urlData.getOriginalURL(), urlData.getShortCode());


        expiryLock.writeLock().lock();
        try {
            expiryIndex.computeIfAbsent(urlData.getExpiresAt(),
                            k -> ConcurrentHashMap.newKeySet())
                    .add(urlData.getShortCode());
        } finally {
            expiryLock.writeLock().unlock();
        }

        codeByUser.computeIfAbsent(urlData.getUserId(),
                        k -> ConcurrentHashMap.newKeySet())
                .add(urlData.getShortCode());
    }

     // Find URL by short code - O(1)
    public URLData findByShortCode(String shortCode) {
        if (shortCode == null) return null;
        return urlByShortCode.get(shortCode);
    }


     // Find short code by original URL - O(1)
    public String findShortCodeByURL(String originalURL) {
        if (originalURL == null) return null;
        return shortCodeByURL.get(originalURL);
    }


    // Check if short code exists - O(1)
    public boolean exists(String shortCode) {
        return urlByShortCode.containsKey(shortCode);
    }

     // Get all URLs for user - O(k) where k = user's URLs
    public List<URLData> findByUserId(String userId) {
        Set<String> codes = codeByUser.get(userId);
        if (codes == null) return Collections.emptyList();

        return codes.stream()
                .map(urlByShortCode::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


     // Delete URL by short code - O(1)
     public void delete(String shortCode) {
         URLData urlData = urlByShortCode.remove(shortCode);
         if (urlData != null) {
             shortCodeByURL.remove(urlData.getOriginalURL());


             expiryLock.writeLock().lock();
             try {
                 Set<String> codesAtExpiry = expiryIndex.get(urlData.getExpiresAt());
                 if (codesAtExpiry != null) {
                     codesAtExpiry.remove(shortCode);
                     if (codesAtExpiry.isEmpty()) {
                         expiryIndex.remove(urlData.getExpiresAt());
                     }
                 }
             } finally {
                 expiryLock.writeLock().unlock();
             }

             Set<String> userCodes = codeByUser.get(urlData.getUserId());
             if (userCodes != null) {
                 userCodes.remove(shortCode);
             }
         }
     }

     // Clean expired URLs - O(n)
     public int cleanExpired() {
         long now = System.currentTimeMillis();
         List<String> toDelete = new ArrayList<>();


         expiryLock.readLock().lock();
         try {
             toDelete = expiryIndex.headMap(now).values().stream()
                     .flatMap(Set::stream)
                     .collect(Collectors.toList());
         } finally {
             expiryLock.readLock().unlock();
         }


         expiryLock.writeLock().lock();
         try {
             for (String code : toDelete) {
                 delete(code);
             }
         } finally {
             expiryLock.writeLock().unlock();
         }

         System.out.println("Cleaned " + toDelete.size() + " expired URLs");
         return toDelete.size();
     }

     // Get most clicked URLs - O(n log k)
    public List<URLData> getTopClickedURLs(int limit) {
        return urlByShortCode.values().stream()
                .sorted((a, b) -> Integer.compare(b.getClicks(), a.getClicks()))
                .limit(limit)
                .collect(Collectors.toList());
    }

}
