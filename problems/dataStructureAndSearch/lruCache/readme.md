Functional Requirement
1 -> it should suppport any type of key-val pair as long as keys are hashable
2 -> it should support Get and Put.
3 -> if cache limit is exhausted it should evict least recently used.
4 -> it should return either null or -1 in case of cache miss
5 -> put operation on existing key treated as recent use

Non-Functional Requirement
1 -> system should be thread safe.
2 -> both get and put operation must runs in O(1) time on avg.
3 -> System should follow OOP with clean separation of resp.
4 -> the internal data structure should be optimized for speed and space with-in the defined constraints


Core Entitits

CacheSystem, Doubly Linked List, HashMap, Node 
