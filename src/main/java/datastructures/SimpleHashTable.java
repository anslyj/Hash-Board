package datastructures;

import java.util.Random;

public class SimpleHashTable implements HashTable {

    private Integer[] table; // Use Integer to allow nulls
    private int capacity;
    private int collisionCount;  // Total number of collisions
    private int insertionCount;  // Total number of successful insert attempts
    private int deletionCount;   // Total number of successful deletions
    private int duplicateCount;  // Insert attempts ignored because key already present

    private int verbose = 0;

    public SimpleHashTable(int size) {
        this.capacity = size;
        this.table = new Integer[size];
        this.collisionCount = 0;
        this.insertionCount = 0;
        this.duplicateCount = 0;

    }

    @Override
    public void setVerbose(int level) {
        this.verbose = level;
    }

    @Override
    public int getCollisions() {
        return collisionCount;
    }

    @Override
    public int getInsertions() {
        return insertionCount;
    }
    @Override
    public boolean insert(Integer key) {
        int index = hash(key);
        insertionCount++;
        for (int i = 0; i < capacity; i++) {
            int tryIndex = (index + i) % capacity;
            if (table[tryIndex] == null) {
                table[tryIndex] = key;
                if( verbose > 0 ) {
                    System.out.println(String.format("Insertion[%3d] = %4d", tryIndex, key ));
                }
                return true;
            } else if (table[tryIndex].equals(key)) {   // duplicate key
                duplicateCount++;
                if (verbose > 0) {
                    System.out.println(String.format("Duplicate [%3d] : %4d", tryIndex, key));
                }
                return false;                           // do not insert
            } else {
                // collision with different key
                if( verbose > 0 ) {
                    System.out.println(String.format("Collision[%3d] : %4d : attempt = %4d", tryIndex, key, i));
                }
                collisionCount++;
            }
        }
        return false; // Table full, insertion not possible
    }

    @Override
    public boolean delete(Integer key) {
        int index = hash(key);
        for (int i = 0; i < capacity; i++) {
            int tryIndex = (index + i) % capacity;
            if (table[tryIndex] != null && table[tryIndex].equals(key)) {
                table[tryIndex] = null;
                deletionCount++;
                return true;
            }
        }
        return false;
    }

    @Override
    public Integer find(Integer key) {
        int index = hash(key);
        for (int i = 0; i < capacity; i++) {
            int tryIndex = (index + i) % capacity;
            if (table[tryIndex] != null && table[tryIndex].equals(key)) {
                return key;
            }
        }
        return null; // Key not found
    }

    private int hash(Integer key) {
        return Math.abs(key.hashCode()) % capacity;
    }

    public void print() {
        System.out.println("Current state of the hash table:");
        for (int i = 0; i < capacity; i++) {
            if (table[i] == null) {
                System.out.println("Slot " + i + ": empty");
            } else {
                System.out.println("Slot " + i + ": " + table[i]);
            }
        }

        System.out.println(String.format("\nTotal collisions :  %4d ", collisionCount ));
        System.out.println(String.format("Total insertions :  %4d ", insertionCount ));
        System.out.println(String.format("Collision rate   :   %6.2f",  (double) collisionCount/(double) insertionCount * 100.00));

    }

    public static void main(String[] args) {
        int numInserts = 20; // Number of random inserts
        int maxRandomValue = 100; // Maximum value for random integers

        SimpleHashTable hashTable = new SimpleHashTable(10); // Initialize hash table with 50 slots
        Random random = new Random();

        for (int i = 0; i < numInserts; i++) {
            int randomKey = random.nextInt(maxRandomValue);
            hashTable.insert(randomKey);
        }
        hashTable.print();
    }

    @Override
    public int size() {
        return insertionCount - deletionCount;
    }

    @Override
    public double averageProbes() {
        return insertionCount == 0 ? 0.0 : 1.0 + (double) collisionCount / insertionCount;
    }
    @Override
    public int getDuplicates() {
        return duplicateCount;
    }
}
