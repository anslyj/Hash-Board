package datastructures;

import java.util.Arrays;
import java.util.function.IntUnaryOperator;

import datastructures.NotImplemented;

/**
 * Open‑address hash table supporting three probe styles:
 *  • LINEAR:      – (h + i) mod m
 *  • QUADRATIC:   – (h + i²) mod m
 *  • DOUBLE_HASH: – (h + i * h₂) mod m
 *
 * A tombstone array lets us mark deletions without breaking search chains.
 *
 *  Author: Hybinette (draft)
 */
public class ProbingHashTable implements HashTable {

    public final Integer[] table;
    public final boolean[] tombstone;
    private final ProbeType  type;
    private final IntUnaryOperator h1;          // primary hash
    private final int size;
    /** rehash when (#live keys) / m > this threshold */
    private static final double LOAD_FACTOR_THRESHOLD = 0.75;

    private int collisions  = 0;
    private int insertions  = 0;
    private int deletions   = 0;
    private int duplicates  = 0;   // ignored duplicate‑key insert attempts

    private int verbose     = 0;

    // ---------- ctor ----------------------------------------------------

    public ProbingHashTable(int size, ProbeType type, char hashCode) {
        this.size      = size;
        this.table     = new Integer[size];
        this.tombstone = new boolean[size];
        this.type      = type;
        this.h1        = chooseHash(hashCode, size);
    }

    // ---------- HashTable API ------------------------------------------

    @Override public int size() { return insertions - deletions; }

    @Override public double averageProbes() {
        return insertions == 0 ? 0.0 : 1.0 + (double) collisions / insertions;
    }


    @Override public boolean insert(Integer key) {
        /*
         * TODO  LINEAR, QUADRATIC and DOUBLE-HASH) INSERT
         *  call probe() method to switch between linear/quadratic and double hashing
         *
         * High-level checklist:
         *   1. Compute the primary bucket index using the table’s hash function.
         *   2. Probe according to the chosen strategy:
         *        • LINEAR:        h, h+1, h+2, ...
         *        • QUADRATIC:     h, h+1², h+2², ...
         *        • DOUBLE-HASH:   h, h+1∙dh, h+2∙dh, ...   (dh = secondHash)
         *   3. First EMPTY slot **or a tombstone slot** → place key, update counters, return true.
         *      *(A **tombstone** is a slot that was deleted earlier; we mark it so the
         *      probe sequence doesn’t break, but it can be reused by a later insert.)*
         *   4. Duplicate key encountered → count duplicate, return false.
         *   5. If you exhaust all m slots → throw IllegalStateException(“Hash table full”).
         *
         * Update these statistics:
         *   insertions  – successful puts
         *   collisions  – times you skipped over an occupied slot
         *   duplicates  – ignored duplicate keys
         */

        int h  = h1.applyAsInt(key);
        int dh = (type == ProbeType.DOUBLE_HASHING)
                ? HashFunctions.secondHash(key, table.length)
                : 1;

        for (int i = 0; i < size; i++) {
            int idx = probe(h, dh, i);
            if (table[idx] != null) {
                // occupied
                if (!tombstone[idx] && table[idx].equals(key)) {
                    duplicates++;
                    return false;
                }
                collisions++;
            } else {
                // empty slot or tombstone
                table[idx] = key;
                tombstone[idx] = false;
                insertions++;
                return true;
            }
        }
        throw new IllegalStateException("Hash table full");
    }

    private int findSlot(Integer key) {
        /*
         * TODO  SEARCH PROBE  (used by find() & delete() below )
         *  call probe() to hop through the table using the current strategy
         *
         * High-level checklist: (1, 2 is very similar to insert())
         *   1. Compute the primary bucket index with the table’s hash function.
         *
         *   2. Loop through table and Probe() according to the chosen strategy:
         *        • LINEAR:        h, h+1, h+2, ...
         *        • QUADRATIC:     h, h+1², h+2², ...
         *        • DOUBLE-HASH:   h, h+1∙dh, h+2∙dh, ...   (dh = secondHash)
         *
         *   3. Stop (and return) as soon as you hit an EMPTY slot that is *not* a tombstone
         *        → the key cannot be anywhere in the table → return -1.
         *
         *   4. If you encounter the key → return the slot index.
         *
         *   5. Otherwise keep probing; after table.length probes return -1
         *        (ensures we never loop forever even if the table is full).
         */
        int h  = h1.applyAsInt(key);
        int dh = (type == ProbeType.DOUBLE_HASHING)
                ? HashFunctions.secondHash(key, table.length)
                : 1;
        for (int i = 0; i < size; i++) {
            int idx = probe(h, dh, i);
            if (table[idx] == null && !tombstone[idx]) {
                return -1;
            }
            if (table[idx] != null && !tombstone[idx] && table[idx].equals(key)) {
                return idx;
            }
        }
        return -1;
    }

    @Override public Integer find(Integer key) {
        // TODO – lookup: use findSlot(key); return key if found, else null.
        int slot = findSlot(key);
        return slot >= 0 ? table[slot] : null;
    }

    /* ---------- DELETE --------------------------------------------------- */
    @Override
    public boolean delete(Integer key) {
        // TODO – delete: locate with findSlot(key); mark tombstone, ++deletions.
        int slot = findSlot(key);
        if (slot < 0) {
            return false;
        }
        table[slot] = null;
        tombstone[slot] = true;
        deletions++;
        return true;
    }

    private int probe(int h, int dh, int i) {
        return switch (type) {
            case LINEAR          -> (h + i)      % size;
            case QUADRATIC       -> (h + i * i)  % size;
            case DOUBLE_HASHING  -> (h + i * dh) % size;
        };
    }

    private static IntUnaryOperator chooseHash(char code, int size) {
        return switch (code) {
            case 'i' -> k -> HashFunctions.division(k, size);
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

    @Override public void print() {
        System.out.println("\n--- ProbingHashTable ---");
        for (int i = 0; i < size; i++) {
            System.out.printf("%3d: %s%n", i,
                    table[i] == null ? (tombstone[i] ? "⊘" : "·")
                            : table[i].toString());
        }
        System.out.printf("insertions  : %d%n", insertions);
        System.out.printf("collisions  : %d%n", collisions);
        System.out.printf("collision%%  : %.2f%n",
                insertions == 0 ? 0.0 : 100.0 * collisions / insertions);
    }

    @Override public void setVerbose(int level) { this.verbose = level; }
    @Override public int  getCollisions() { return collisions; }
    @Override public int  getInsertions() { return insertions; }
    @Override public int getDuplicates() { return duplicates; }


    /* ------------------------------------------------------------------ */
    /*  Rehash helpers                                                    */
    /* ------------------------------------------------------------------ */

    /** Doubles the table size (next odd number) and reinserts live keys. */
    private void rehash() {
        int newSize = size * 2 + 1;          // simple grow policy
        Integer[] oldTable     = table.clone();
        boolean[] oldTombstone = tombstone.clone();

        Arrays.fill(table, null);
        Arrays.fill(tombstone, false);
        collisions = 0;                      // reset stats
        insertions = 0;
        deletions  = 0;

        for (int i = 0; i < oldTable.length; i++) {
            if (oldTable[i] != null && !oldTombstone[i]) {
                insertInternal(oldTable[i]); // helper that skips LF test
            }
        }
    }

    /** Insert used internally during rehashing; ignores LF check. */
    private void insertInternal(Integer key) {
        int h  = h1.applyAsInt(key);
        int dh = (type == ProbeType.DOUBLE_HASHING)
                ? HashFunctions.secondHash(key, table.length)
                : 1;
        for (int i = 0; i < table.length; i++) {
            int idx = probe(h, dh, i);
            if (table[idx] == null) {
                table[idx] = key;
                insertions++;
                return;
            }
        }
        throw new IllegalStateException("Rehash failed: table too small");
    }

    /**
     * Runs 500‐insert experiments on m=101 for:
     *   – Hash codes: division(i), mult(m), random(r), midsquare(s), folding(o)
     *   – Probe types: LINEAR, QUADRATIC, DOUBLE_HASHING
     *   – Key patterns: Uniform, Clustered
     *
     * Prints a TSV: Hash\tPattern\tProbe\tCollisions\tDuplicates\tAvgProbes
     */
    public static void main(String[] args) {
        final int M = 101, N = 500;
        java.util.Random rng = new java.util.Random(42);

        // Build the two key-lists
        java.util.List<Integer> uniform = new java.util.ArrayList<>(N);
        for (int i = 0; i < N; i++) uniform.add(rng.nextInt(50_000));

        java.util.List<Integer> clustered = new java.util.ArrayList<>(N);
        for (int i = 0; i < N/2; i++) clustered.add(rng.nextInt(500));
        for (int i = 0; i < N/2; i++) clustered.add(8_000 + rng.nextInt(500));

        java.util.Map<String,java.util.List<Integer>> patterns = java.util.Map.of(
                "Uniform", uniform,
                "Clustered", clustered
        );

        // The five hash codes and three probe styles
        char[] hashCodes = {'i','m','r','s','o'};
        ProbeType[] probes = {
                ProbeType.LINEAR,
                ProbeType.QUADRATIC,
                ProbeType.DOUBLE_HASHING
        };

        // Header
        System.out.printf("Hash\tPattern\tProbe\tCollisions\tDuplicates\tAvgProbes%n");

        // Run each combo
        for (char code : hashCodes) {
            for (var entry : patterns.entrySet()) {
                String pattern = entry.getKey();
                for (ProbeType pt : probes) {
                    ProbingHashTable tbl = new ProbingHashTable(M, pt, code);
                    for (int k : entry.getValue()) {
                        try {
                            tbl.insert(k);
                        } catch (IllegalStateException full) {
                            break;  // table full
                        }
                    }
                    System.out.printf(
                            "%c\t%s\t%s\t%d\t%d\t%.3f%n",
                            code,
                            pattern,
                            pt,
                            tbl.getCollisions(),
                            tbl.getDuplicates(),
                            tbl.averageProbes()
                    );
                }
            }
        }
    }
}
