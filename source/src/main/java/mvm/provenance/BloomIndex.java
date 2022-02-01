package mvm.provenance;

import com.skjegstad.utils.BloomFilter;

import java.util.List;
import java.util.Set;


/**
 * Generic interface for all Bloofi-like data structures.
 *
 * @param <E>
 * @author Daniel Lemire
 */
public interface BloomIndex<E> {
    public int deleteFromIndex(int id, InsDelUpdateStatistics stat);

    /**
     * Return the size - number of bits in a Bloom Filter indexed by this
     * index
     *
     * @return
     */
    public int getBloomFilterSize();

    public int getHeight();

    public Set<Integer> getIDs();

    public boolean getIsRootAllOne();

    public int getNbChildrenRoot();

    /**
     * Return the number of nodes in this Bloom Index
     *
     * @return
     */
    public int getSize();

    public void insertBloomFilter(BloomFilter<E> bf, InsDelUpdateStatistics stat);

    /**
     * Return matching ids
     */
    public List<Integer> search(E o, SearchStatistics stat);

    // TODO: it is not clear why we need an id parameter here?
    public int updateIndex(BloomFilter<E> newBloomFilter, InsDelUpdateStatistics stat);
}
