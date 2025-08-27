package datastructures;

import java.util.LinkedList;
import java.util.List;

public class SeparateChainingHashTable implements HashTable {

    /* —— state —— */
    public final LinkedList<Integer>[] table;   // exposed for GUI visualiser
    private final java.util.function.IntUnaryOperator hashFn;
    private int collisions = 0, insertions = 0;
    private int deletions  = 0;

    private int duplicates = 0;      // number of duplicate inserts ignored

    private int verbose = 0;


    @SuppressWarnings("unchecked")
    public SeparateChainingHashTable(int size, char hashCode) {
        table = new LinkedList[size];
        for (int i = 0; i < size; i++) table[i] = new LinkedList<>();

        hashFn = switch (hashCode) {
            case 'i' -> k -> HashFunctions.division(k, size);   // NEW letter
            case 'm' -> k -> HashFunctions.multiplication(k, size);
            case 's' -> k -> HashFunctions.midsquare(k, size);
            case 'o' -> k -> HashFunctions.folding(k, size);
            case 'f' -> k -> HashFunctions.fibonacci(k, size);
            case 'r' -> k -> HashFunctions.random(k, size);
            case 'c' -> k -> HashFunctions.custom(k, size);
            case 'd' -> k -> HashFunctions.some_other_hash_function(k, size);
            default  -> k -> HashFunctions.division(k, size);
        };
    }

    /* ==== HashTable API =================================================== */
    @Override public boolean insert(Integer key) {
        int idx = hashFn.applyAsInt(key);
        LinkedList<Integer> bucket = table[idx];

        if (bucket.contains(key)) {                      // duplicate key
            duplicates++;
            return false;
        }
        //if (bucket.contains(key)) return false;          // ignore duplicates

        if (!bucket.isEmpty()) { collisions++; }         // collision ⇔ bucket non-empty
        bucket.addFirst(key);
        insertions++;

        if (verbose > 0)
            System.out.printf("Insert %-5d -> slot %-3d  (bucket size %d)%n",
                    key, idx, bucket.size());
        return true;
    }

    @Override public boolean delete(Integer key) {
        int idx = hashFn.applyAsInt(key);
        boolean removed = table[idx].remove(key);
        if (removed) deletions++;
        if (verbose > 0)
            System.out.printf("Delete %-5d : %s%n", key,
                    removed ? "OK" : "not found");
        return removed;
    }

    @Override public Integer find(Integer key) {
        int idx = hashFn.applyAsInt(key);
        boolean found = table[idx].contains(key);
        if (verbose > 1)
            System.out.printf("Search %-5d : %s%n", key,
                    found ? "found" : "not found");
        return found ? key : null;
    }

    @Override public void print() {
        System.out.println("\nCurrent state of the hash table:");
        for (int i = 0; i < table.length; i++) {
            System.out.printf("Slot %2d: %s%n", i,
                    table[i].isEmpty() ? "empty" : table[i]);
        }
        System.out.printf("%nTotal collisions : %d%n", collisions);
        System.out.printf("Total insertions : %d%n", insertions);
        System.out.printf("Collision rate   : %.2f%n",
                insertions == 0 ? 0.0 : 100.0 * collisions / insertions);
    }

    @Override public int getCollisions() { return collisions; }

    @Override public int getInsertions() { return insertions; }

    @Override public void setVerbose(int level) { verbose = level; }
    @Override
    public int size() {
        return insertions - deletions;
    }

    @Override
    public double averageProbes() {
        return insertions == 0 ? 0.0 : 1.0 + (double) collisions / insertions;
    }

    @Override
    public int getDuplicates() {
        return duplicates;
    }
}