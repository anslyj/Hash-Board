package datastructures;

/** Convenience wrapper: open-address table with quadratic probing. */
public class QuadraticProbingHashTable extends ProbingHashTable {

    public QuadraticProbingHashTable(int size, char hashCode) {
        super(size, ProbeType.QUADRATIC, hashCode);
    }

    public QuadraticProbingHashTable(int size) {
        this(size, 'd');
    }
}