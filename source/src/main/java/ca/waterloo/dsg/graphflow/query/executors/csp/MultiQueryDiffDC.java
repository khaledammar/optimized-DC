package ca.waterloo.dsg.graphflow.query.executors.csp;

import ca.waterloo.dsg.graphflow.util.Report;

import java.util.*;

/**
 * Instead of representing a diff for each query, we want to share the overhead among queries.
 * In this class, the map key is the iteration, but the value is
 * This is a class that represent a data structure to store diffs permanently as we process
 * multiple batches. We do not want to interact with the map directly because we want to encapsulate the details of
 * how the data structure of diffs are implemented inside this class.
 */
public class MultiQueryDiffDC {

    // (`iter`:short, `pos`:int) stored as 2 consecutive ints in int[] array. Index 0 is always the count of entries.
    // `pos` points to a position in the `diffsStore` shared with the `DistancesDC` class.
    Map<Integer, int[]> sharedDiffs;

    /**
     * @param q = number of queries
     */
    public MultiQueryDiffDC() {
        sharedDiffs = new HashMap<>();
    }

    /**
     * A function that returns a set of all vertices with one or more diffs
     *
     * @return
     */
    public Set<Integer> getVerticesWithDiff() {
        return sharedDiffs.keySet();
    }

    public void mergeDeltaDiffs(int q, Map<Integer, int[]> deltaDiffs, List<short[]> diffsStore,
                                List<Integer> diffsPool) {
        for (var entry : deltaDiffs.entrySet()) {
            var vertex = entry.getKey();
            var newDistances = entry.getValue();
            var oldDistances = sharedDiffs.get(vertex);
            if (oldDistances != null) {
                // Delete old diffs.
                int limit = oldDistances[0] * 2;
                for (int i = 2; i <= limit; i += 2) {
                    int pos = oldDistances[i];
                    if (pos == diffsStore.size() - 1) {
                        diffsStore.remove(pos);
                    } else {
                        diffsPool.add(pos);
                        diffsStore.set(pos, null);
                    }
                }
            }
            if (newDistances[0] == 0) {
                sharedDiffs.remove(vertex);
            } else {
                sharedDiffs.put(vertex, newDistances);
            }
        }
    }

    public void mergeDeltaDiffsJOD(int q, Map<Integer, int[]> deltaDiffs, List<short[]> diffsStore,
                                 List<Integer> diffsPool) {
        for (var entry : deltaDiffs.entrySet()) {
            var vertex = entry.getKey();
            var newDistances = entry.getValue();
            var oldDistances = sharedDiffs.get(vertex);
            if (oldDistances != null) {
                int limit = oldDistances[0] * 2;
                for (int i = 2; i <= limit; i += 2) {
                    var pos = oldDistances[i];
                    if (pos == diffsStore.size() - 1) {
                        diffsStore.remove(pos);
                    } else {
                        diffsPool.add(pos);
                        diffsStore.set(pos, null);
                    }
                }
            }
            var limit = newDistances[0] * 2;
            if (limit == 0) {
                newDistances = null;
            } else {
                var s = new HashMap<Long, Short>();
                int windex = 1;
                for (int i = 1; i <= limit; i += 2) {
                    int iter = newDistances[i];
                    int pos = newDistances[i + 1];
                    short[] diffs = diffsStore.get(pos);
                    var limit2 = diffs[0] * 5 + 1;
                    s.clear();
                    for (int j = 1; j < limit2; j += 5) {
                        var distance = Distances.getDistanceFromArray(diffs, j);
                        var diff = diffs[j];
                        var y = s.get(distance);
                        if (y != null) {
                            diff = (short) (diff + y);
                        }
                        s.put(distance, diff);
                    }
                    diffs[0] = (short) s.size();
                    var index = 1;
                    for (var sentry : s.entrySet()) {
                        var d = sentry.getValue();
                        if (d == 0) {
                            diffs[0]--;
                            continue;
                        }
                        Distances.setDistanceToArray(diffs, sentry.getKey(), index);
                        diffs[index] = d;
                        index += 5;
                    }
                    if (diffs[0] == 0) {
                        if (pos == diffsStore.size() - 1) {
                            diffsStore.remove(pos);
                        } else {
                            diffsPool.add(pos);
                            diffsStore.set(pos, null);
                        }
                        newDistances[0]--;
                    } else {
                        newDistances[windex] = iter;
                        newDistances[windex + 1] = pos;
                        windex += 2;
                    }
                }
                if (newDistances[0] == 0) {
                    newDistances = null;
                }
            }
            if (newDistances == null) {
                sharedDiffs.remove(vertex);
            } else {
                sharedDiffs.put(vertex, newDistances);
            }
        }
    }

    public int[] getDiffs(int vertex) {
        var x = sharedDiffs.get(vertex);
        if (x == null) {
            DistancesDC.emptyList2[0] = 0;
            x = DistancesDC.emptyList2;
        }
        return x;
    }

    public int[] getDiffsCopied(int vertex, List<short[]> diffStore) {
        var distances = sharedDiffs.get(vertex);
        if (distances != null) {
            distances = distances.clone();
            int limit = distances[0] * 2;
            for (int i = 2; i <= limit; i += 2) {
                diffStore.add(diffStore.get(distances[i]).clone());
                distances[i] = diffStore.size() - 1;
            }
            return distances;
        } else {
            return new int[]{0, 0, 0};
        }
    }

    public void print(List<short[]> diffStore) {
        /*
        for (var entry : sharedDiffs.entrySet()) {
            Report.INSTANCE.debug(entry.getKey() + " --> [" + DistancesDC.distancesString(entry.getValue(), diffStore) + "]");
        }

         */
    }
}
