package ca.waterloo.dsg.graphflow.query.executors.csp;

import java.util.LinkedHashMap;
import java.util.Map;


public class DiffCache<K, V> extends LinkedHashMap<K, V> {

    private int cacheSize;

    public DiffCache(int cacheSize) {
        super(16, (float) 0.75, true);
        this.cacheSize = cacheSize;
    }


    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() >= cacheSize;
    }
}
