package mvm.provenance;

import java.util.Random;

/**
 * Implements simple tabulation hashing.
 *
 * @author Daniel Lemire
 */
public class Hasher {
    static Random r;
    int[] randomkeys = new int[0];
    int maxval = 0;

    public Hasher() {
        r = new Random();
    }

    public Hasher(int seed) {
        r = new Random(seed);
    }

    public Hasher(int seed, int maxval) {
        r = new Random(seed);
        this.maxval = maxval;
    }

    public int getNumberOfHashFunctions() {
        return randomkeys.length;
    }

    public int hash(Object o, int whichhash) {
        return (((o.hashCode() * randomkeys[whichhash])) & Integer.MAX_VALUE) % maxval;
    }

    /**
     * Should be called as soon as we know how many hash functions are
     * needed. If called again with a different number of hash functions,
     * and exception is thrown. (The number of hash functions should be
     * constant.)
     *
     * @param K
     */
    public void setNumberOfRandomKeys(int K) {
        if (randomkeys.length > 0) {
            if (K != randomkeys.length) {
                throw new RuntimeException("You are changing the number of hash functions?");
            }
            return;
        }
        randomkeys = new int[K];
        for (int i = 0; i < randomkeys.length; ++i) {
            randomkeys[i] = (r.nextInt() >>> 2) * 2 + 1;
        }
    }


    public void setMaxValue(int bitSetSize) {

        /**
         * I do not need this check because
         */
        if (maxval != 0) {
            if (bitSetSize != maxval) {
                throw new RuntimeException("Resizing dynamically is not  supported");
            }
        }
        maxval = bitSetSize;
    }
}
