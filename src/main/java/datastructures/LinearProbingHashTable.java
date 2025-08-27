package datastructures;

/** Convenience wrapper: open-address table with linear probing. */
public class LinearProbingHashTable extends ProbingHashTable {

    public LinearProbingHashTable(int size, char hashCode) {
        super(size, ProbeType.LINEAR, hashCode);
    }

    /** Defaults to division hash if caller omits hash-code letter. */
    public LinearProbingHashTable(int size) {
        this(size, 'd');
    }
}