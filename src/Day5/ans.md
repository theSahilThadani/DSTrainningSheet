### **1. How to handle hash collisions?**
    
* When two different keys hash to the same index:
    * fist way is store multiple entries in one place in linked list
    * Hash "sahil" → index 5
      Index 5 already has "parthiv"
      Add "sahil" to the linked list at index 5
      To find "sahil", traverse the list and compare keys
    * second way using tree data structure
    * HashMap uses a combination of chaining and balanced trees when a bucket has many collisions typically when 8>items it converts to a Red Black tree for better performance O(log n) for n items.


---
    

### **2. Show to support prefix search (e.g., “am*”)?**

* we can use tree data structure to use prefix search
    * Time Complexity: O(k) where k = length of prefix
      Space: O(n * m) where n = number of users, m = average username length
* we can use TreeMap and store in sorted manner and get result based on sorting eg: NavigableMap<String, String> results =
  users.subMap("am", "an"); where we can get result starting from am>= and less than <an
    * Time Complexity: O(log n + k) where k = number of matches
      Better than: Scanning all entries in HashMap
* we can also go for hybrid approach using trie and hashmap 


### **2. Can this scale to millions of users?**
 * 1M users so 100MB++ data to be stored and increases collisions also resize 1M entries is expensive
 * first solution is we can optimize load factor 0.5 to minimize collisions but requires double space. eg: HashMap<String, String> users = new HashMap<>(2_000_000);
 * for 1M we have Use ConcurrentHashMap For Multi-threaded Access
 * we can make partition to store user in different eg:-  hashmaps HashMap 1: Users a-h HashMap 2: Users i-p HashMap 3: Users q-z
 * we can use better hash function and design hashing strategy
 * we can use database and caching techniques.

