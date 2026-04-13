# Low-Level Design: Handling Concurrency
---

## 1. Core Problem: Race Conditions
A race condition occurs when multiple threads access shared mutable state simultaneously, leading to inconsistent data.

### Common Patterns
* **Double-Assignment:** Two threads assign different objects to the same resource (e.g., two cars assigned to one parking spot).
* **Lost Update:** A shared counter is updated non-atomically (e.g., two threads decrementing seat counts, resulting in overselling).
* **Inconsistent State:** An object’s state changes between the time it is "checked" and the time it is "acted upon."



---

## 2. Concurrency Toolset

| Tool | Granularity | Best Use Case |
| :--- | :--- | :--- |
| **Synchronized / Mutex** | Entire Method | Protecting "Check-then-Act" sequences. |
| **Per-Resource Locks** | Individual Resource | High-throughput systems (e.g., Movie Ticket Booking). |
| **Concurrent Collections** | Single Operation | Thread-safe lookups (e.g., `ConcurrentHashMap`). |
| **Atomic Operations** | Single Value | Counters and ID generation (`AtomicInteger`). |

---

## 3. Implementation Patterns (Java)

### Pattern A: The Synchronized Method (The 80% Solution)
This is the simplest way to close the "Check-then-Act" gap.

```java
public class ParkingLot {
    private List<ParkingSpot> spots;

    // The 'synchronized' keyword ensures only one thread 
    // executes this logic at a time for this instance.
    public synchronized Ticket parkVehicle(Vehicle vehicle) {
        for (ParkingSpot spot : spots) {
            if (spot.isAvailable()) { // Check
                spot.assignVehicle(vehicle); // Act
                return new Ticket(vehicle, spot);
            }
        }
        return null; // No spots available
    }
}
```

### Pattern B: Per-Resource Locking (High Throughput)
Use this when locking the entire class becomes a performance bottleneck.

```java
import java.util.concurrent.locks.ReentrantLock;

public class Show {
    private Map<String, Seat> seats;
    private final Map<String, ReentrantLock> seatLocks = new ConcurrentHashMap<>();

    public boolean bookSeat(String seatId) {
        // Ensure a lock exists for the specific seat
        ReentrantLock lock = seatLocks.computeIfAbsent(seatId, k -> new ReentrantLock());

        // Try to acquire the lock without blocking other users indefinitely
        if (lock.tryLock()) {
            try {
                Seat seat = seats.get(seatId);
                if (seat.isAvailable()) {
                    seat.setBooked(true);
                    return true;
                }
            } finally {
                lock.unlock();
            }
        }
        return false;
    }
}
```

---

## 4. Interview Strategy: When to Add Concurrency?

1.  **Does the prompt mention "Thread-Safety" or "Multiple Users"?** → **Yes**, build it in.
2.  **Is it a Game Model (Chess/Tic-Tac-Toe)?** → **No**, focus on OO Design unless asked.
3.  **Are you low on time?** → **No**, implement the core logic first, then add `synchronized` as a follow-up.

> **The 2-Minute Retrofit Rule:** If your code is clean and modular, you can usually make it thread-safe by simply adding the `synchronized` keyword to your primary execution methods.

---

## 5. Summary Table

| Scenario | Recommended Approach |
| :--- | :--- |
| **Parking Lot / Meeting Room** | `synchronized` methods. |
| **Inventory / Counters** | `AtomicInteger` or `LongAdder`. |
| **Booking Systems (Seats/Rooms)** | Explicit `ReentrantLock` per resource. |
| **Distributed Systems** | Optimistic Locking (Version columns/CAS). |