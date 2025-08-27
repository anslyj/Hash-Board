package datastructures;

/** Convenience wrapper: open-address table with double hashing. */
public class DoubleHashingHashTable extends ProbingHashTable {

    public DoubleHashingHashTable(int size, char hashCode) {
        super(size, ProbeType.DOUBLE_HASHING, hashCode);
    }

    public DoubleHashingHashTable(int size) {
        this(size, 'd');
    }
}