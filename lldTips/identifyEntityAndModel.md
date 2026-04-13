# LLD Cheat Sheet: Identifying Entities & Modeling Relationships

---

## 1. The Noun-Verb Filtering Technique
Extract nouns and verbs from the requirements, then filter them to avoid "God Classes" and redundant entities.

### Filtering Rules
* **Discard System Concepts:** "Platform," "System," or "Application" are usually the context, not entities.
* **Attributes vs. Classes:** If a noun is a simple property (Price, ID, Name), treat it as an attribute.
* **Enums for Fixed Sets:** If values are known at compile time (BookingStatus, SeatType), use an Enum.
* **Merge Overlaps:** If two nouns describe the same lifecycle (Ticket & Booking), merge them.



---

## 2. The Entity Decision Tree
Once a noun is identified, use this logic to determine its implementation type:

1.  **Fixed set of values?** → `Enum`
2.  **Has its own state/identity?** → `Attribute` (if No) / `Class` (if Yes)
3.  **Multiple implementations?** → `Interface` (no shared state) or `Abstract Class` (shared state/logic)

| Construct | Has State? | Multiple Impls? | Example |
| :--- | :--- | :--- | :--- |
| **Enum** | No | No | `VehicleType`, `OrderStatus` |
| **Concrete Class** | Yes | No | `User`, `Theater`, `Movie` |
| **Abstract Class** | Yes (shared) | Yes | `Vehicle`, `Notification` |
| **Interface** | No | Yes | `PricingStrategy`, `PaymentGateway` |

---

## 3. Modeling Relationships
Relationships define **Ownership** and **Lifecycle Coupling**.



| Relationship | Coupling | Ownership | Analogy |
| :--- | :--- | :--- | :--- |
| **Composition** | Strong | Whole owns Part | Room in a House (House dies, Room dies) |
| **Aggregation** | Moderate | Whole uses Part | Student in a Class (Class ends, Student lives) |
| **Association** | Weak | Peer-to-Peer | Doctor and Patient (Independent lifecycles) |
| **Dependency** | Weakest | Temporary Use | Printer used by a Document |

---

## 4. Implementation Example (Java)

The following code demonstrates all four relationship types within a single domain.

```java
import java.util.*;

// Interface for Dependency Injection
interface PricingStrategy {
    double calculate(long duration);
}

class Vehicle { /* Aggregation: Exists outside the lot */ }

class ParkingFloor { /* Composition: Owned by the lot */ }

public class ParkingLot {
    // 1. COMPOSITION: Floors are created and destroyed with the Lot
    private final List<ParkingFloor> floors;
    
    // 2. DEPENDENCY: Strategy is injected; Lot depends on it to function
    private PricingStrategy pricingStrategy;

    // 3. AGGREGATION: Lot "has" a vehicle temporarily, but doesn't own it
    private Map<String, Vehicle> parkedVehicles;

    public ParkingLot(int floorCount, PricingStrategy strategy) {
        this.floors = new ArrayList<>();
        for (int i = 0; i < floorCount; i++) {
            this.floors.add(new ParkingFloor()); // Construction = Ownership
        }
        this.pricingStrategy = strategy;
        this.parkedVehicles = new HashMap<>();
    }

    // 4. ASSOCIATION: Parking produces a Ticket (Interaction link)
    public Ticket park(Vehicle vehicle) {
        this.parkedVehicles.put("A1", vehicle);
        return new Ticket(vehicle, "A1"); 
    }
}

class Ticket {
    private Vehicle vehicle;
    private String spotId;
    public Ticket(Vehicle v, String s) { this.vehicle = v; this.spotId = s; }
}
```

---

## 5. Summary Cheat Sheet

* **Composition:** `part = new Part()` inside the constructor.
* **Aggregation:** `this.part = passedInPart` (stored in a field).
* **Dependency:** `method(Part p)` (used only within a method scope).
* **Association:** `Ticket t = service.create(user, show)` (linking two independent entities).