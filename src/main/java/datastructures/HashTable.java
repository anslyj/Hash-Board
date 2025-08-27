package datastructures;

public interface HashTable {
    boolean insert(Integer key);
    boolean delete(Integer key);
    Integer find(Integer key);

    /** Pretty print the table plus statistics. */
    void print();

    /** Adjust console verbosity (0-silent, 1-ops, 2-ops+searches). */
    void setVerbose(int level);

    /* Basic metrics so Driver can compare tables */
    int getCollisions();
    int getInsertions();

    /**
     * Number of duplicate-key insert attempts that were ignored.
     * Default 0 so existing implementations compile until they add tracking.
     */
    default int getDuplicates() { return 0; }

    /* ---------- GUI helpers (optional) ------------------------------ */

    /** Current number of elements in the table. */
    int size();

    /** Average probes per successful insert (1 + collisions / inserts). */
    double averageProbes();
}