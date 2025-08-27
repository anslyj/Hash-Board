package datastructures;

/**
 * TODOs for the extra credit assignment:
 *  1. Implement the following hash functions so they distribute keys well:
 *     • multiplication(int k, int size)
 *     • folding(int key, int m)
 *     • foldingFour(int k, int size)
 *     • random(int key, int m)
 *     • midsquare(int k, int size)
 *
 *  2. Leave division() and fibonacci() as‑is (already implemented).
 *
 *  Uncomment/replace the UnsupportedOperationException in each method
 *  body once you've written the code.
 */

import java.util.Random; // helper that records unimplemented calls
import datastructures.NotImplemented;

/** Five simple hash functions that operate on an int key. */
public final class HashFunctions {

    private HashFunctions() { }

    /* --- classic hashes -------------------------------------------------- */
    public static int division(int key, int m) {
        return Math.floorMod(key, m);          // handles negatives
    }

    /* --- */

    public static int multiplication(int k, int size) {      // ⌊ m (kA mod 1)⌋
        // Knuth's multiplicative method
        final double A = (Math.sqrt(5) - 1) / 2;      // fractional constant
        double product = k * A;
        double frac = product - Math.floor(product);
        return (int) (size * frac);
    }

    /* —— folding (3-digit groups) —— */
    public static int folding(int key, int m) {
        int sum = 0;
        int n = Math.abs(key);
        while (n > 0) {
            sum += n % 1000;  // 3-digit block
            n /= 1000;
        }
        return sum % m;
    }

    public static int foldingFour(int k, int size) {             // fold 4-digit blocks
        int sum = 0;
        int n = Math.abs(k);
        while (n > 0) {
            sum += n % 10000; // 4-digit block
            n /= 10000;
        }
        return sum % size;
    }

    /* —— random (consistent per run) —— */
    private static final Random RAND = new Random(123456789);
    private static final java.util.Map<Integer,Integer> randomMap = new java.util.HashMap<>();
    public static int random(int key, int m) {
        Integer existing = randomMap.get(key);
        if (existing != null) {
            return existing;
        }
        int bucket = RAND.nextInt(m);
        randomMap.put(key, bucket);
        return bucket;
    }

    public static int midsquare(int k, int size) {           // middle bits of k²
        long square = (long) k * (long) k;
        // number of bits needed to represent size-1
        int neededBits = 32 - Integer.numberOfLeadingZeros(size - 1);
        int shift = (Long.SIZE - neededBits) / 2;
        long mask = (1L << neededBits) - 1;
        int mid = (int) ((square >>> shift) & mask);
        return mid % size;
    }

    public static int fibonacci(int k, int size) {           // φ-based hashing
        final double φ = (1 + Math.sqrt(5)) / 2;
        return (int) Math.floor(size *
                ((k * (φ - 1)) % 1));
    }

    /* --- helpers for double hashing ------------------------------------- */

    /** Second-hash must never return 0; we make it odd so it’s coprime to a power-of-two table size. */
    public static int secondHash(int k, int size) {
        return 1 + Math.floorMod(midsquare(k, size - 2), size - 1);
    }

    /* —— custom: mix bits —— */
    public static int custom(int key, int m) {
        int h = key;
        h ^= (h >>> 20) ^ (h >>> 12);
        h ^= (h >>> 7)  ^ (h >>> 4);
        return Math.floorMod(h, m);
    }

    /* ——  double hashing —— */
    public static int some_other_hash_function(int key, int m) {
        int h = key;
        // optional extra hash
        // e.g., mixBits or other
        return custom(key, m);
    }
}
