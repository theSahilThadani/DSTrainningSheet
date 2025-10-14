### **1. How to implement shuffle mode efficiently?**

* A **Random Pointer Array** or **ArrayList reference** of all nodes for quick random access.
* Shuffle the references using **cCollections.shuffle()** O(n) and play songs in that order.
* Another method-> assign each node a **random weight** and sort based on that for random order.
* Maintain a **visited flag** if we dont want to repeat songs before all are played.

---

### **2. How to merge two playlists quickly?**

* If both are **singly linked lists**, merging is O(1).

    * Simply link the `tail` of the first list to the `head` of the second list.
    * `tail1.next = head2;`
* No need to copy data both playlists remain accessible in combined order.

---

### **3. Are there cache/memory issues compared to arrays?**

* **Yes**, linked lists are less cache-friendly

    * Arrays store elements **contiguously** in memory better CPU cache utilization.
    * Linked lists store nodes **scattered in memory** extra pointer dereferencing causes **cache misses**.
* Each node requires **extra memory** for the pointer.
