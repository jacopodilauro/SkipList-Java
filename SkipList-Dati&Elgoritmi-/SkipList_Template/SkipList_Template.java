import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

// Classe MyEntry
class MyEntry {
    private Integer key;
    private String value;
    public MyEntry(Integer key, String value) {
        this.key = key;
        this.value = value;
    }
    public Integer getKey() {
        return key;
    }
    public String getValue() {
		return value;
    }
    @Override
    public String toString() {
		return key + " " + value;
    }
}

// Classe Node
class Node<T> {
    private T entry;
    private Node<T> below;
    private Node<T> above;
    private Node<T> next;
    private Node<T> previous;

    public Node(T entry) {
        this.entry = entry;
    }

    public T getEntry() {
        return entry;
    }

    public Node<T> getBelow() {
        return below;
    }

    public void setBelow(Node<T> below) {
        this.below = below;
    }

    public Node<T> getAbove() {
        return above;
    }

    public void setAbove(Node<T> above) {
        this.above = above;
    }

    public Node<T> getNext() {
        return next;
    }

    public void setNext(Node<T> next) {
        this.next = next;
    }

    public Node<T> getPrevious() {
        return previous;
    }

    public void setPrevious(Node<T> previous) {
        this.previous = previous;
    }
}

// Classe SkipList
class SkipList {
    private Node<MyEntry> head;
    private Node<MyEntry> tail;
    private long totalTraversedNodes;
	
	// Costruttore
    public SkipList() {
		head = new Node<>(new MyEntry(Integer.MIN_VALUE, null));
        tail = new Node<>(new MyEntry(Integer.MAX_VALUE, null));
        head.setNext(tail);
        tail.setPrevious(head);
        totalTraversedNodes = 0;
	}
    
    // Restituisce il numero totale di nodi attraversati
    public long getTotalTraversedNodes() {
    	return totalTraversedNodes;
    }
	
	// Implementazione di SkipSearch
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
    
    // Conta il numero di nodi attraversati per effettuare un'operazione di inserimento
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
	
	// Implementazione di SkipInsert
	public int skipinsert(Integer key, String value, int height) {
        Node<MyEntry> position = skipsearch(key);
        Node<MyEntry> newNode = new Node<>(new MyEntry(key, value));
		int traversedNodes = countNodes(key);
		
        // Inserimento nella lista principale della nuova entry
        newNode.setNext(position.getNext());
        newNode.setPrevious(position);
        position.getNext().setPrevious(newNode);
        position.setNext(newNode);
		
        // Creazione di livelli superiori
        int i = 0;
        while (i < height) {
            Node<MyEntry> newLevelNode = new Node<>(new MyEntry(key, value));
			
            // Troviamo il nodo da collegare al livello superiore:
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
	
	// Verifica se la SkipList è vuota
    public boolean isEmpty() {
    	Node<MyEntry> current = head;
        while (current.getBelow() != null) {
            current = current.getBelow();
        }
        return (current.getNext() == tail);
    }

	// Restituisce la entry con chiave minima
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
    
    // Rimuove la entry con chiave minima
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
		
        // Rimuovi il nodo dalla lista principale
        minNode.getPrevious().setNext(minNode.getNext());
        minNode.getNext().setPrevious(minNode.getPrevious());

        // Pulizia dei livelli superiori
        while (minNode.getAbove() != null) {
            minNode = minNode.getAbove();
            minNode.getPrevious().setNext(minNode.getNext());
            minNode.getNext().setPrevious(minNode.getPrevious());
        }

        return minEntry;
    }

	// Stampa il contenuto della SkipList
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

// Classe SkipListPQ
class SkipListPQ {

    private double alpha;
    private Random rand;
    private SkipList s;
    private long totalTraversedNodes = 0;
    private int insertCount = 0;
    
    public SkipListPQ(double alpha) {
        this.alpha = alpha;
        this.rand = new Random();
        this.s = new SkipList();
    }

    public int size() {
		return s.size();
    }
    
    public double getAverageTraversedNodes() {
    	return (double) (s.getTotalTraversedNodes()) / (insertCount);
    }
    
    public int getInsertCount() {
    	return insertCount;
    }

    public MyEntry min() {
		return s.getMin().getEntry();
    }

    public int insert(int key, String value) {
    	insertCount++;
    	int height = generateEll(alpha, key);
        return s.skipinsert(key, value, height);
    }

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
	
    public MyEntry removeMin() {
    	return s.removeMin();
    }

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
