### **1. How to handle packets arriving out-of-order?**

* Use packet **sequence numbers** to store them in the correct order.
* Keep track of the **next expected sequence** and delay playback until all prior packets arrive. ex: we can keep next expected sequence nt nextExpectedSequence = 1, and we can delay playback till sequence received.  
* and we can also use a **PriorityQueue** or **TreeMap** to auto-sort by sequence.

---

### **2. How to manage limited memory?**

* **Resize dynamically** we are increase size by double so we are getting time complexity as O(1) not exactly but Amortized value.
* **Release unused references** to help garbage collection **eg: array[size-1] = null**.
* Use **circular buffers** or **maximum limits** to reuse memory efficiently like we can set fix size of buffer and after exceeding the limit we can resue buffer from start as they are utilized by client.

---

### **3. Could a linked list be used instead?**

* Yes, for frequent insertions and deletions.
* But **access speed is O(n)** compared to **O(1)** in arrays. and also extra over-head per node like we have to store the next and prev pointer also.
* Arrays (or ArrayLists) are more suitable for **ordered, index-based** data like packets.
