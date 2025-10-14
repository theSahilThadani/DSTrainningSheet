### **1. What if the number of seats is unknown?**

If the number of seats is unknown, we can implement a **dynamic array** or use an **`ArrayList`** in Java.
`ArrayList` allows us to add or remove elements dynamically without needing to know the size in advance.


### **2. How to handle multiple bookings at the same time?**

To handle multiple bookings at the same time, we can use:

* The **`synchronized`** keyword to make critical sections thread-safe.
* **`ReentrantLock`** for finer control over synchronization.
* **`AtomicIntegerArray`** to perform atomic seat booking operations safely.

These methods help prevent **race conditions** and **duplicate bookings**.


### **3. Would a linked list be better in some cases?**

We can use a **LinkedList**, but retrieval operations would take **O(n)** time for both singly and doubly linked lists.
However, insertion and deletion operations are **dynamic and efficient**, and the structure isn’t fixed in size.
