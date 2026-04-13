
## 📝 Project: Autocomplete Suggestion System

### 1. Requirements (Core Constraints)
* **Normalization:** All input must be treated as **lowercase**; the system is case-insensitive.
* **Character Set:** The system supports **English characters only** (ASCII/a-z).
* **Configurable Ranking:** Suggestions can be ranked by **Frequency** or **Alphabetical order**. This must be user-configurable.
* **Pagination/Limit:** Default return is **10 suggestions**, but this limit must be configurable.
* **Frequency Logic:** Frequency increments only during **word insertion**, not during a search/lookup.
* **Dynamic Vocabulary:** The system must support adding new words dynamically at runtime.

### 2. Functional Requirements
* **Insertion:** `insert(String word)` – Adds a word to the system or increments its frequency if it already exists.
* **Search:** `getSuggestions(String prefix)` – Returns a list of strings based on the current ranking strategy.
* **Configuration:** `setStrategy(Strategy type)` and `setLimit(int k)` – Allows real-time updates to the system behavior.
* **Scaling:** The dictionary size must grow gracefully as new words are inputted.

### 3. Non-Functional Requirements
* **Modularity:** Use the **Strategy Pattern** to make ranking logic interchangeable and testable.
* **Efficiency:** Use a **Trie (Prefix Tree)** data structure to ensure $O(L)$ search time, where $L$ is the length of the prefix.
* **Low Latency:** Auto-complete results should be returned in near real-time (sub-100ms).
* **SOLID Principles:** Clear **Separation of Concerns** between the data storage (Trie), the ranking logic (Strategy), and the client-facing service.

---

### 🛠️ Recommended Design Patterns
To meet these requirements, you should plan to implement:

| Pattern | Purpose |
| :--- | :--- |
| **Trie Data Structure** | Efficient prefix searching and storage. |
| **Strategy Pattern** | For switching between `FrequencyRanking` and `AlphabeticalRanking`. |
| **Singleton / Factory** | To manage the instance of the Autocomplete engine. |

### 💡 Interview Tip: The "Top-K" Optimization
If the interviewer asks how to handle millions of words without the `getSuggestions` method becoming slow (because of sorting), mention that you can store a **pre-computed list of the top 10 words** at every node in the Trie.
