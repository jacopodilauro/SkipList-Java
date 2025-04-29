import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Represents an entry node within the Skip List.
 * Each entry holds a key-value pair and pointers to adjacent nodes
 * (next, previous, above, below) to facilitate Skip List operations.
 *
 * @author Jacopo
 */
class MyEntry {
    protected Integer key;		// Key of the Entry
    private String value;		// Value of the Entry
    protected MyEntry valuE;    // Entry used for counting traversed nodes
    protected MyEntry next;		// Next Entry
    protected MyEntry prev;		// Previous Entry
    protected MyEntry above;	// Entry above
    protected MyEntry below;	// Entry below
    protected int h;			// Height of the Entry
    
/**
     * Constructs a standard entry with a key and a value.
     * Used for regular data nodes in the Skip List.
     *
     * @param key   The integer key for this entry.
     * @param value The string value associated with the key.
     */
    public MyEntry(Integer key, String value) {
        this.key = key;
        this.value = value;
    }
    
/**
     * Constructs a special entry used by the {@code skipSearch} method.
     * The 'key' parameter stores the number of nodes traversed during the search,
     * and the 'valuE' parameter stores the actual entry found at the base level.
     *
     * @param key   The number of nodes traversed during a search operation.
     * @param valuE The actual {@code MyEntry} node found at the base level after the search.
     */
    public MyEntry(Integer key, MyEntry valuE) { // key -> number of iterations
    	this.key = key;
    	this.valuE = valuE;
    }
    
/**
     * Returns the key of this entry.
     *
     * @return The integer key.
     */
    public Integer getKey() {
        return key;
    }
    
 /**
     * Returns the value of this entry.
     * Returns null if this entry was constructed using the special constructor
     * for search results.
     *
     * @return The string value, or null.
     */
    public String getValue() {
        return value;
    }
    
/**
     * Sets the height of this entry's tower (at the base level).
     *
     * @param h The height to set.
     */
    public void setH(int h) {
    	this.h = h;
    }
    
    // @return The height.
    public int getH() {
    	return h;
    }
    
    // @return A string in the format "key value"
    public String toString() {
        return key + " " + value;
    }
}

/**
 * Implements a Skip List data structure that also functions as a Priority Queue.
 * It supports insertion, finding the minimum element, and removing the minimum element.
 * It maintains statistics about the number of operations performed and the average
 * number of nodes traversed during insertions.
 */
class SkipListPQ {
	
    private double alpha;        // Alpha parameter
    private Random rand;         // Random number generator
    private int level;           // Maximum level
    private int size;            // Number of elements
    protected MyEntry head;      // First element at the top left
    protected MyEntry tail;      // Last element at the top right
    private MyEntry start;       // Bottom left position
    private MyEntry tail_start;  // Bottom right element
    protected int numExe;        // Execution count
    protected double averageIt;  // Average number of traversed nodes
    protected long numItTot;     // Total number of iterations
    
 /**
     * Constructs an empty Skip List. Initializes sentinel nodes and sets up
     * the basic structure with one level.
     *
     * @param alpha The probability factor (between 0.0 and 1.0) for level generation,
     * or a value outside this range to use deterministic height generation based on key divisibility by 2.
     */
    public SkipListPQ(double alpha) {
        this.alpha = alpha;
        this.rand = new Random();
        this.head = new MyEntry(Integer.MIN_VALUE, (String) null);
        this.tail = new MyEntry(Integer.MAX_VALUE, (String) null);
        this.start = new MyEntry(Integer.MIN_VALUE, (String) null);
        this.tail_start = new MyEntry(Integer.MAX_VALUE, (String) null);
        
        // Setting up initial elements and default pointers
        this.numExe = 0;
        this.averageIt = 0;
        this.numItTot = 0;
        head.next = tail;
        head.prev = null;
        head.above = null;
        head.below = start;
        tail.prev = head;
        tail.next = null;
        tail.above = null;
        tail.below = tail_start;
        start.next = tail_start;
        start.prev = null;
        start.below = null;
        start.above = head;
        tail_start.next = null;
        tail_start.prev = start;
        tail_start.below = null;
        tail_start.above = tail;
        size = 0;
        level = 1;        
    }

 /**
     * Returns the next entry at the same level.
     *
     * @param Entry The current entry.
     * @return The next entry, or null if it's the last one.
     */
    public MyEntry next(MyEntry Entry) {
        return Entry.next;
    }
    
/**
     * Returns the previous entry at the same level.
     *
     * @param Entry The current entry.
     * @return The previous entry, or null if it's the first one.
     */
    public MyEntry prev(MyEntry Entry) {
        return Entry.prev;
    }
	
/**
     * Returns the entry directly above the given entry.
     *
     * @param Entry The current entry.
     * @return The entry above, or null if it's at the top level.
     */
    public MyEntry above(MyEntry Entry) {
        return Entry.above;
    }
    
 /**
     * Returns the entry directly below the given entry.
     *
     * @param Entry The current entry.
     * @return The entry below, or null if it's at the base level.
     */
    public MyEntry below(MyEntry Entry) {
        return Entry.below;
    }
	
 /**
     * Returns the number of actual data entries in the Skip List.
     *
     * @return The size of the Skip List.
     */
    public int size() {
        return size; 
    }
    
  /**
     * Returns the total number of insertion operations performed.
     *
     * @return The insertion count.
     */
	public int numExe() {
		return numExe;
	}
	
 /**
     * Finds the entry with the minimum key in the Skip List.
     * Traverses down from the head sentinel to the base level and returns the first actual data entry.
     *
     * @return The {@code MyEntry} with the minimum key, or {@code tail_start} if the list is empty.
     */
    public MyEntry min() {	
        MyEntry head2 = head;
        while(below(head2) != null){
        	head2 = below(head2);        	
        }       
        return next(head2); 
    }
	
	/**
     * Inserts a new key-value pair into the Skip List.
     * 1. Uses {@code skipSearch} to find the correct position for the new element at the base level
     * and counts the traversed nodes.
     * 2. Generates a random height (level) for the new element's tower based on {@code alpha}
     * or deterministically based on the key.
     * 3. Inserts the new node at the base level and potentially creates nodes in upper levels
     * up to the generated height, adjusting pointers accordingly.
     * 4. If the generated height exceeds the current maximum level of the Skip List,
     * new sentinel levels are added.
     * 5. Increments the size.
     * 6. Updates statistics (execution count, total iterations, average iterations).
     *
     * @param key   The key of the element to insert.
     * @param value The value associated with the key.
     * @return The number of nodes traversed during the initial search phase of this insertion.
     */
    public int insert(int key, String value){
		MyEntry pp = skipSearch(key);
		MyEntry p = pp.valuE;   // Entry determining the insert position
		MyEntry q = null;		
		MyEntry s = head;       // Adjust head if necessary for increased height
		MyEntry t;
		
		int h = generateEll(alpha, key); 
		
		int i = -1;
		while(i < h){
			i = i +1;
			if(i >= level){
				level++;
				t= s.next;
				s = insertAfterAbove(null, s, Integer.MIN_VALUE, null);
				insertAfterAbove(s, t, Integer.MAX_VALUE, null);
			}
			head = s;
			q = insertAfterAbove(p, q, key, value);
			while(p!=null && p.above == null){
            	p = p.prev;
            }
            if(p!=null){
            	p = p.above;
            }
		}
		
		size++;
		while(below(q) != null) {
			q = below(q);
		}
		
		q.setH(h+1);
		numExe++;		
		numItTot += pp.key;
		averageIt = (double) numItTot / numExe;
        return pp.key;
	}
	
/**
     * Helper method to insert a new node after a given node {@code af} (after)
     * and link it vertically to a node {@code ab} (above).
     * This correctly sets the {@code next}, {@code prev}, {@code above}, and {@code below} pointers.
     *
     * @param af  The node after which the new node should be inserted horizontally. Can be null (for head sentinels).
     * @param ab  The node which will be below the new node. Can be null (for the first node in a tower).
     * @param key The key for the new node.
     * @param s   The value for the new node.
     * @return The newly created and inserted {@code MyEntry}.
     */
    public MyEntry insertAfterAbove(MyEntry af, MyEntry ab, int key, String s) {
        MyEntry newNode = new MyEntry(key, s);
        if (af != null) {
            newNode.next = af.next;
            if (af.next != null) {
                af.next.prev = newNode;
            }
            af.next = newNode;
            newNode.prev = af;
        }
        if (ab != null) {
            newNode.below = ab;
            ab.above = newNode;
        }
        return newNode;  
    }
	
	/**
     * Searches for the position where a key should be inserted or located.
     * Starts from the top-left sentinel ({@code head}) and traverses down and right.
     * At each level, it moves right as long as the next node's key is less than the target key.
     * When it can no longer move right, it moves down to the next level.
     * It counts the number of nodes visited during this traversal.
     *
     * @param key The key to search for.
     * @return A special {@code MyEntry} where:
     * - {@code getKey()} returns the number of nodes traversed during the search.
     * - {@code valuE} field holds the {@code MyEntry} at the base level that precedes
     * the position where the key should be inserted (or the node with the key if found).
     */
    public MyEntry skipSearch(int key) {
        MyEntry p = head;
        MyEntry pp;
        
        int numIt = 1; // count the firts <node>
        while(below(p) != null) {
            p = below(p);
            numIt++;
            while(key >= next(p).getKey()) {
                p = next(p);
                numIt++;
            }
        }
        pp = new MyEntry(numIt, p);
        return pp;
    }
	
/**
     * Generates a height (level) for a new node being inserted.
     * The height determines how many levels the node will span.
     * Two strategies:
     * 1. Probabilistic (if {@code alpha_} is [0, 1)): The level increases with probability {@code alpha_}.
     * The resulting level follows a geometric distribution.
     * 2. Deterministic (if {@code alpha_} is outside [0, 1)): The level is determined by the number
     * of times the key is divisible by 2 (trailing zeros in binary representation).
     *
     * @param alpha_ The alpha parameter passed during Skip List construction.
     * @param key    The key of the node being inserted (used for deterministic strategy).
     * @return The generated height (0-based index, e.g., 0 means only base level).
     */
    private int generateEll(double alpha_, int key) {
        int level = 0;
        if (alpha_ >= 0. && alpha_ < 1) {
            while (rand.nextDouble() < alpha_) {
                level += 1;
            }
        } else {
            while (key != 0 && key % 2 == 0) {
                key = key / 2;
                level += 1;
            }
        }
        return level; 
    }
	
	/**
     * Removes the entry with the minimum key from the Skip List.
     * 1. Finds the minimum entry using {@code min()}.
     * 2. Adjusts the {@code next} and {@code prev} pointers of the adjacent nodes
     * at the base level to bypass the minimum node.
     * 3. Traverses upwards through the tower of the minimum node, adjusting pointers
     * at each level similarly.
     * 4. Decrements the size.
     * (Note: This implementation doesn't currently handle shrinking the number of levels if the top becomes empty).
     *
     * @return The {@code MyEntry} that was removed (the minimum element), or {@code null} if the list was empty.
     */
    public MyEntry removeMin() {
        
        
        MyEntry minEntry = min();  
        if (minEntry != null) {
            MyEntry current = minEntry;
            
            
            if (current.prev != null) {
                current.prev.next = current.next;
            }
            if (current.next != null) {
                current.next.prev = current.prev;
            }
    
            while (current.above != null) {
                current = current.above;
                if (current.prev != null) {
                    current.prev.next = current.next;
                }
                if (current.next != null) {
                    current.next.prev = current.prev;
                }
            }
        }   
        size--;
        return minEntry;
    }
    

    

/**
     * Prints the elements of the Skip List at the base level in ascending order of keys.
     * Includes the key, value, and the height (number of levels) of each node's tower.
     * If the list is empty, prints a message indicating so.
     */
	public void print(){
		if(size > 0) {
			MyEntry head2 = head;				
		    while(below(head2) != null){
		    	head2 = below(head2);        	
		    }
		    head2 = next(head2);       	
		    System.out.print(head2 + " " + head2.getH());
		    for(int i = 1; i < size(); i++){
		    	head2 = next(head2);       	
		    	System.out.print(", " + head2 + " " + head2.getH());
		    }
		    System.out.println();
		}else {  
			System.out.println("Empty list, try again");
		}
		
	}
	
}

/**
 * Main class to test the SkipListPQ implementation.
 * Reads operations from a specified file and executes them on a SkipListPQ instance.
 * The input file format:
 * - First line: N alpha (N = number of operations, alpha = skip list parameter)
 * - Subsequent N lines: operation [key value]
 * - operation 0: Print minimum element
 * - operation 1: Remove minimum element
 * - operation 2: Insert key value
 * - operation 3: Print entire list (base level)
 * Finally, prints statistics about the Skip List execution.
 */

public class SkipList_Base {
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
                    	System.out.println(skipList.min()); 
                        break;
                    case 1:
                    	skipList.removeMin();                
                        break;
                    case 2:
						skipList.insert(Integer.parseInt(line[1]), line[2]); 
                        break;
                    case 3:
						skipList.print();
                        break;
                    default:
                        System.out.println("Invalid operation code");
                        return;
                }
            }
            System.out.println(alpha + " " + skipList.size() + " " + skipList.numExe + " " + skipList.averageIt);
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }        
    }
}
