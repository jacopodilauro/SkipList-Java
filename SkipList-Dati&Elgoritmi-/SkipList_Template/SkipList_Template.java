/**
 * SkipList_Template.java
 *
 * Demonstrates a skip list-based priority queue, including entry,
 * node, skip list, and priority queue classes, and a main test driver.
 *
 * @author jacopo
 * @version 2.0
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Represents a key-value pair stored in the skip list.
 */
class MyEntry {
    private Integer key;
    private String value;
    
    /**
     * Constructs an entry with the given key and value.
     * @param key   the priority key
     * @param value the associated value string
     */
    public MyEntry(Integer key, String value) {
        this.key = key;
        this.value = value;
    }
    /**
     * Returns the entry's key.
     * @return the integer key
     */
    public Integer getKey() {
        return key;
    }
    /**
     * Returns the entry's value.
     * @return the string value
     */
    public String getValue() {
		return value;
    }
    /**
     * Returns a string representation of the entry.
     * @return "key value" format
     */
    public String toString() {
		return key + " " + value;
    }
}

/**
 * Generic node for skip list, supporting vertical and horizontal links.
 * @param <T> the type of entry stored in the node
 */
class Node<T> {
    private T entry;
    private Node<T> below;
    private Node<T> above;
    private Node<T> next;
    private Node<T> previous;

    /**
     * @param entry
     */
    public Node(T entry) {
        this.entry = entry;
    }

    /**
     * @return emtry
     */
    public T getEntry() {
        return entry;
    }

    /**
     * @return below
     */
    public Node<T> getBelow() {
        return below;
    }

    /**
     * @param below
     */
    public void setBelow(Node<T> below) {
        this.below = below;
    }

    /**
     * @return above
     */
    public Node<T> getAbove() {
        return above;
    }

    /**
     * @param above
     */
    public void setAbove(Node<T> above) {
        this.above = above;
    }

    /**
     * @return next
     */
    public Node<T> getNext() {
        return next;
    }

    /**
     * @param next
     */
    public void setNext(Node<T> next) {
        this.next = next;
    }

    /**
     * @return previus
     */
    public Node<T> getPrevious() {
        return previous;
    }

    /**
     * @param previous
     */
    public void setPrevious(Node<T> previous) {
        this.previous = previous;
    }
}

/**
 * Skip list implementation supporting search, insertion, and removal
 * with probabilistic balancing.
 */
class SkipList {
    private Node<MyEntry> head;
    private Node<MyEntry> tail;
    private long totalTraversedNodes;
	
	/**
     * Initializes an empty skip list with sentinel head and tail.
     */
    public SkipList() {
		head = new Node<>(new MyEntry(Integer.MIN_VALUE, null));
        tail = new Node<>(new MyEntry(Integer.MAX_VALUE, null));
        head.setNext(tail);
        tail.setPrevious(head);
        totalTraversedNodes = 0;
	}
    
    /** @return cumulative nodes traversed across inserts */
    public long getTotalTraversedNodes() {
    	return totalTraversedNodes;
    }
	
	/**
     * Finds position preceding the given key, top-down.
     * @param key target key
     * @return node at which to insert below
     */
    public Node<MyEntry> skipsearch(Integer key) {
        Node<MyEntry> current = head;
        while (current != null) {
            while (current.getNext().getEntry().getKey() <= key) {
                current = current.getNext();
            }
            if (current.getBelow() != null) {
                current = current.getBelow();
            } else {
                break;
            }
        }
        return current;
    }
    
     /**
     * Counts nodes visited during search for diagnostics.
     * @param key target key
     * @return count of traversed nodes
     */
    public int countNodes(Integer key) {
        Node<MyEntry> current = head;
        int nodes = 2;
        while (current != null) {
            while (current.getNext().getEntry().getKey() <= key) {
                current = current.getNext();
                nodes++;
            }
            if (current.getBelow() != null) {
                current = current.getBelow();
                nodes++;
            } else {
                break;
            }
        }
        return nodes;
    }
	
/**
 * Inserts a new entry with the given key and value into the skip list.
 * The height of the new node's tower above the base level is determined
 * by the provided {@code height} parameter. This method finds the correct
 * horizontal position at each level and links the new node accordingly.
 * It also updates the total count of traversed nodes during the search.
 *
 * @param key    The integer key of the new entry, representing its priority.
 * @param value  The string value associated with the new entry.
 * @param height The number of levels (above the base level) for the new node's tower.
 * @return The number of nodes traversed during the search and insertion process.
 */
	public int skipinsert(Integer key, String value, int height) {
        Node<MyEntry> position = skipsearch(key);
        Node<MyEntry> newNode = new Node<>(new MyEntry(key, value));
		int traversedNodes = countNodes(key);
		
        // insert at base level
        newNode.setNext(position.getNext());
        newNode.setPrevious(position);
        position.getNext().setPrevious(newNode);
        position.setNext(newNode);
		
        // build towers
        int i = 0;
        while (i < height) {
            Node<MyEntry> newLevelNode = new Node<>(new MyEntry(key, value));
			
            while (position.getAbove() == null && position.getPrevious() != null) {
                position = position.getPrevious();
            }
            position = position.getAbove();

            if (position == null) {
                Node<MyEntry> newHead = new Node<>(new MyEntry(Integer.MIN_VALUE, null));
                Node<MyEntry> newTail = new Node<>(new MyEntry(Integer.MAX_VALUE, null));
                newHead.setNext(newTail);
                newTail.setPrevious(newHead);
                newHead.setBelow(head);
                head.setAbove(newHead);
                newTail.setBelow(tail);
                tail.setAbove(newTail);
                head = newHead;
                tail = newTail;
                position = head;
            }

            newLevelNode.setBelow(newNode);
            newNode.setAbove(newLevelNode);

            newLevelNode.setNext(position.getNext());
            newLevelNode.setPrevious(position);
            position.getNext().setPrevious(newLevelNode);
            position.setNext(newLevelNode);

            newNode = newLevelNode;
            i++;
        }
        
        totalTraversedNodes += traversedNodes;
        return traversedNodes;
    }
	
	/** @return true if no elements exist */
    public boolean isEmpty() {
    	Node<MyEntry> current = head;
        while (current.getBelow() != null) {
            current = current.getBelow();
        }
        return (current.getNext() == tail);
    }

	/**
     * Peeks at the minimum entry without removal.
     * @return node containing the smallest key
     */
    public Node<MyEntry> getMin() {
        if (isEmpty()) {
            return null;
        }
        
        Node<MyEntry> current = head;
        while (current.getBelow() != null) {
            current = current.getBelow();
        }
        Node<MyEntry> minNode = current.getNext();
		return minNode;
    }
    
    /**
     * Removes and returns the minimum entry.
     * @return removed entry or null if empty
     */
    public MyEntry removeMin() {
        if (isEmpty()) {
            return null;
        }
        
		Node<MyEntry> current = head;
        while (current.getBelow() != null) {
            current = current.getBelow();
        }
        Node<MyEntry> minNode = current.getNext();
		MyEntry minEntry = minNode.getEntry();
		
        minNode.getPrevious().setNext(minNode.getNext());
        minNode.getNext().setPrevious(minNode.getPrevious());

        while (minNode.getAbove() != null) {
            minNode = minNode.getAbove();
            minNode.getPrevious().setNext(minNode.getNext());
            minNode.getNext().setPrevious(minNode.getPrevious());
        }

        return minEntry;
    }

    /** Prints all entries in ascending order with tower heights. */
    public void print() {
        if (isEmpty()) {
            System.out.println("SkipList is empty.");
            return;
        }
		
        Node<MyEntry> current = head;
        while (current.getBelow() != null) {
            current = current.getBelow();
        }
        current = current.getNext();

		String s = "";
        while (current.getEntry().getKey() != Integer.MAX_VALUE) {
            int count = 0;
            Node<MyEntry> temp = current;
            while (temp != null) {
                count++;
                temp = temp.getAbove();
            }
            s += current.getEntry().toString() + " " + count + ", ";
            current = current.getNext();
        }

        System.out.println(s.substring(0, s.length() - 2));
    }
	/** @return number of stored entries */
    public int size() {
        Node<MyEntry> current = head;
        while (current.getBelow() != null) {
            current = current.getBelow();
        }
        if (current.getNext() == tail) return 0;
        else {
        	int size = - 1;
		    while (current.getNext() != null) {
		        size++;
		        current = current.getNext();
		    }
		    return size;
        }
    }
}

/**
 * Priority queue based on SkipList, using probability alpha for level growth.
 */
class SkipListPQ {

    private double alpha;
    private Random rand;
    private SkipList s;
    private long totalTraversedNodes = 0;
    private int insertCount = 0;
    
    /**
     * Initializes the priority queue with the given alpha parameter.
     * @param alpha level-up probability (0 <= alpha < 1)
     */
    public SkipListPQ(double alpha) {
        this.alpha = alpha;
        this.rand = new Random();
        this.s = new SkipList();
    }
/** @return current number of entries */
    public int size() {
		return s.size();
    }
    
    /** @return average nodes traversed per insert */
    public double getAverageTraversedNodes() {
    	return (double) (s.getTotalTraversedNodes()) / (insertCount);
    }

    /** @return insertCount */
    public int getInsertCount() {
    	return insertCount;
    }

    /** @return s.getMin().getEntry() */
    public MyEntry min() {
		return s.getMin().getEntry();
    }

    /**
     * Inserts a new key-value pair into the priority queue.
     * The height of the newly inserted node in the underlying
     * skip list is determined probabilistically based on the
     * {@code alpha} parameter and the provided {@code key}.
     *
     * @param key   The integer key to insert, representing the priority.
     * @param value The string value associated with the key.
     * @return The number of nodes traversed during the insertion operation
     * in the underlying skip list.
     */
    public int insert(int key, String value) {
    	insertCount++;
    	int height = generateEll(alpha, key);
        return s.skipinsert(key, value, height);
    }
	/**
     * Generates tower height based on alpha probability.
     */
    private int generateEll(double alpha_, int key) {
        int level = 0;
        if (alpha_ >= 0 && alpha_ < 1) {
          while (rand.nextDouble() < alpha_) {
              level += 1;
          }
        }
        else {
          while (key != 0 && key % 2 == 0) {
            key = key / 2;
            level += 1;
          }
        }
        return level;
    }

    /**
     * Removes and returns the entry with the minimum key (highest priority)
     * from the priority queue.
     *
     * @return The {@code MyEntry} object with the minimum key that was removed,
     * or {@code null} if the priority queue is empty.
     */
    public MyEntry removeMin() {
        return s.removeMin();
    }

    /**
     * Prints the contents of the priority queue to the standard output.
     * This method relies on the underlying skip list's print functionality
     * to display the entries in ascending order of their keys, along with
     * the height of each node's tower.
     */
    public void print() {
        s.print();
    }
}

// TestProgram
public class SkipList_Template {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java TestProgram <file_path>");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
            String[] firstLine = br.readLine().split(" ");
            int N = Integer.parseInt(firstLine[0]);
            double alpha = Double.parseDouble(firstLine[1]);
            System.out.println(N + " " + alpha);

            SkipListPQ skipList = new SkipListPQ(alpha);

            for (int i = 0; i < N; i++) {
                String[] line = br.readLine().split(" ");
                int operation = Integer.parseInt(line[0]);

                switch (operation) {
                    case 0:
                    	MyEntry minEntry = skipList.min();
                    	if (minEntry != null) System.out.println(minEntry.toString());
                        break;
                    case 1:
                    	skipList.removeMin();
                        break;
                    case 2:
						int key = Integer.parseInt(line[1]);
                        String value = line[2];
                        skipList.insert(key, value);
                        break;
                    case 3:
                    	skipList.print();
                        break;
                    default:
                        System.out.println("Invalid operation code");
                        return;
                }
            }

            System.out.println(alpha + " " + skipList.size() + " " + skipList.getInsertCount() + " " + skipList.getAverageTraversedNodes());
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
}
