package LLDProblems.dataStructureAndSearch.lruCache;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;


class Node<K, V> {
    private final K key;
    private V value;
    private Node<K, V> next;
    private Node<K, V> prev;

    public Node(K key, V val){
        this.key = key;
        this.value = val;
        next = null;
        prev = null;
    }

    public void setVal(V val){
        this.value = val;
    }

    public V getVal() {
        return value;
    }

    public K getKey() {
        return key;
    }

    public Node<K,V>getNext(){
        return next;
    }

    public Node<K,V> getPrev(){
        return prev;
    }

    public void setNext(Node<K,V>node){
        this.next = node;
    }

    public void setPrev(Node<K,V>node){
        this.prev = node;
    }
}

class DoublyLinkedList<K,V> {
    private final Node<K,V> head;
    private final Node<K,V> tail;

    public DoublyLinkedList(){
        head = new Node<>(null, null);
        tail = new Node<>(null, null);
        head.setNext(tail);
        tail.setPrev(head);
    }

    public void addNodeFirst(Node<K,V>node){
        Node<K,V>actualHead = head.getNext();
        node.setNext(actualHead);
        actualHead.setPrev(node);

        node.setPrev(head);
        head.setNext(node);
    }

    private void removeNode(Node<K,V>node){
        Node<K,V> nextNode = node.getNext();
        Node<K,V>prevNode = node.getPrev();

        node.setNext(null);
        node.setPrev(null);
        nextNode.setPrev(prevNode);
        prevNode.setNext(nextNode);
    }

    public void removeNodeFromTail(){
        Node<K,V>actualTail = tail.getPrev();
        if(head.equals(actualTail)){
            return;
        }

        removeNode(actualTail);  
    }

    public void makeNodeRecent(Node<K,V>node){
        removeNode(node);
        addNodeFirst(node);
    }

    public Node<K,V> getTailNode() {
        return tail.getPrev();
    }
}


class LRUCache<K,V> {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final DoublyLinkedList<K,V> doublyLinkedList;
    private final Map<K, Node<K,V>>map;
    private int size;

    public LRUCache(DoublyLinkedList<K,V>doublyLinkedList, Map<K, Node<K,V>> map, int size){
        this.doublyLinkedList = doublyLinkedList;
        this.map = map;
        this.size = size;
    }

    private void removeLRUNode(){
        Node<K,V> lastNode = doublyLinkedList.getTailNode();
        K lastKey = lastNode.getKey();
        map.remove(lastKey);
        doublyLinkedList.removeNodeFromTail();
    }

    private void updateNode(K key, V val) {
        Node<K,V> node = map.get(key);
        node.setVal(val);
        doublyLinkedList.makeNodeRecent(node);
    }

    public synchronized void add(K key, V val){
        if(map.containsKey(key)){
            updateNode(key, val);            
            return;
        }

        Node<K,V> node = new Node<>(key, val);
        if(map.size() == size) {
           removeLRUNode();
        }

        map.put(key, node);
        doublyLinkedList.addNodeFirst(node);
    }

    public V get(K key) {
        lock.writeLock().lock(); // We use writeLock because we modify the list order
        try {
            if(map.containsKey(key)){
                Node<K,V> node = map.get(key);
                doublyLinkedList.makeNodeRecent(node);
                return node.getVal() ;
            }
            return null;
        } finally {
            lock.writeLock().lock(); // We use writeLock because we modify the list order
        }
    }
} 


public class LRUCacheApp {

    public static void main(String[] args){
        DoublyLinkedList<Integer, String> doublyLinkedList = new DoublyLinkedList<>();
        Map<Integer, Node<Integer, String>>map = new ConcurrentHashMap<>();
        LRUCache<Integer, String>cache = new LRUCache<>(doublyLinkedList, map, 3);
        cache.add(1,  "abhi");
        cache.add(2, "kr");
        cache.add(3, "nope");
        System.out.println("value is: " + cache.get(2));
        cache.add(2, "Kumar");
        System.out.println("value is: " + cache.get(2));
    }
    
}
