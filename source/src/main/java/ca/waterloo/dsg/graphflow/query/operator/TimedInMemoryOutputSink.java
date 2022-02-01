package ca.waterloo.dsg.graphflow.query.operator;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Created by zaidshennar on 2017-06-12.
 */
public class TimedInMemoryOutputSink {
    private BatchedOutput counts = new BatchedOutput();

    public void append() {
        counts.increment();
    }

    public void incrementBatch() {
        counts.incrementBatch();
    }

    public void setBatchTime(long timeOfLastBatch) {
        counts.setTime(timeOfLastBatch);
    }

    public BatchedOutput getCounts() {
        return counts;
    }

    public int getTotal() {
        return counts.getTotal();
    }

    public double getAverage() {
        return counts.getAverage();
    }

    @Override
    public String toString() {
        return counts.toString();
    }

    public class BatchedOutput {

        private Map<Integer, MutableInt> counts = new HashMap<>();
        private int batch_index = 0;
        private int total = 0;

        void increment() {
            total++;
            counts.get(batch_index).increment();
        }

        void incrementBatch() {
            batch_index++;
            counts.put(batch_index, new MutableInt());
        }

        int getTotal() {
            return total;
        }

        double getAverage() {
            long total =
                    counts.keySet().stream().mapToInt(batch -> batch).mapToLong(batch -> counts.get(batch).getTime())
                            .sum();
            return total * 1.0 / counts.size();
        }

        void setTime(long time) {
            counts.get(batch_index).setTime(time);
        }

        @Override
        public String toString() {
            StringJoiner stringJoiner = new StringJoiner(System.lineSeparator());
            stringJoiner.add("Batch,Add,Delete,Total,Time (ms)");
            int count = 0;
            for (int result : counts.keySet()) {
                stringJoiner.add(result + ";" + counts.get(result).add + ";" + counts.get(result).delete + ";" +
                        counts.get(result).value + ";" +
                        String.format("%.2f", counts.get(result).getTime() / 1000000.0));
                count++;
                if (count >= 500) {
                    break;
                }
            }
            return stringJoiner.toString();
        }
    }

    public class MutableInt {

        int value = 0;
        int add = 0;
        int delete = 0;
        long time;

        void increment() {
            ++value;
        }

        public int getValue() {
            return value;
        }

        long getTime() {
            return time;
        }

        void setTime(long time) {
            this.time = time;
        }
    }
}
