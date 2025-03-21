import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

// Class representing an entry in the Skip List
class MyEntry {
    protected Integer key;		// Key of the Entry
    private String value;		// Value of the Entry
    protected MyEntry valuE;    // Entry used for counting traversed nodes
    protected MyEntry next;		// Next Entry
    protected MyEntry prev;		// Previous Entry
    protected MyEntry above;	// Entry above
    protected MyEntry below;	// Entry below
    protected int h;			// Height of the Entry
    
    // Constructor for MyEntry
    public MyEntry(Integer key, String value) {
        this.key = key;
        this.value = value;
    }
    
    // Constructor for MyEntry with node count modification
    public MyEntry(Integer key, MyEntry valuE) { // key -> number of iterations
    	this.key = key;
    	this.valuE = valuE;
    }
    
    // Returns the key
    public Integer getKey() {
        return key;
    }
    
    // Returns the value
    public String getValue() {
        return value;
    }
    
    // Sets the height
    public void setH(int h) {
    	this.h = h;
    }
    
    // Returns the height
    public int getH() {
    	return h;
    }
    
    // Prints the entry
    public String toString() {
        return key + " " + value;
    }
}

// Class implementing the Skip List with Priority Queue functionality
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
    
    // Builder of the skip list
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

	// Returns the next
    public MyEntry next(MyEntry Entry) {
        return Entry.next;
    }
    
    // Returns the previous entry
    public MyEntry prev(MyEntry Entry) {
        return Entry.prev;
    }
	
	// Returns the entry above
    public MyEntry above(MyEntry Entry) {
        return Entry.above;
    }
    
    // Returns the entry below
    public MyEntry below(MyEntry Entry) {
        return Entry.below;
    }
	
	// Returns the size of the Skip List
    public int size() {
        return size; 
    }
    
    // Returns the number of executions
	public int numExe() {
		return numExe;
	}
	
	 // Finds the minimum entry in the Skip List
    public MyEntry min() {	
        MyEntry head2 = head;
        while(below(head2) != null){
        	head2 = below(head2);        	
        }       
        return next(head2); 
    }
	
	/*
     * Inserimento dell'elemento,
     * 1- uso SkiSearch per trovare la posizione giusta per inserire il nuovo elemento
     * 2- genero un livello casuale per il nuovo elemento e modifico i vari elementi succssivi, precedenti, sopra  sottostanti
     * 3- eventualmente modifico head  l'altezza massima
     * 4- modifico 'size'
     * 5- esegui i calcoli per la media degli attraversamenti tra nodi 
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
	
	// Adjust the next, above one
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
	
	/*
     * Ricerca,
     * 1- parto da 'head', e eseguo lo skipSearch aumentando il numero dei nodi passati
     *    ad ogni movimento
     * 2- ritorno una Entry che e' composta dal numero di nodi passati (key) e dalla Entry trovata (value)
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
	
	// Create casual heaight
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
	
	/*
     * Rimuovere il minimo,
     * 1- ottengo il minimo
     * 2- modifico l'attorno dle minimo e lo elimino
     * 3- modifico i livelli sopra
     * 4- riduco la dimensione di uno
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
    

    

	// Prints the Skip List
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

//TestProgram

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

