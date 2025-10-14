### **1. Searching 1000 tabs: Is linked list efficient?**

* Not really searching is O(n) because we must traverse each node sequentially.
* For 1000+ tabs, performance drops due to no direct indexing.
* Arrays or hash maps are better for frequent searches.

---

### **2. Would arrays or trees be better?**

* **Arrays:**

    * Great for **index based access** and cache performance.
    * Poor for frequent insert adn delete operations in the middle.
* **Trees :**

    * Efficient for **searching and sorting (O(log n))**.
* **Linked List:**

    * Best for **dynamic insertion and deletion** .

---

#### **3. How does memory fragmentation affect performance?**

* Linked lists store nodes **non-contiguously** so which increases **memory fragmentation**.
* This causes:

    * **Poor cache locality** CPU has to jump between memory addresses.
    * **Slower traversal speed**.
* Arrays being **contiguous** use memory more efficiently.
