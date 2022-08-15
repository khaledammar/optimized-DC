package ca.waterloo.dsg.graphflow.query.plans;

import ca.waterloo.dsg.graphflow.ExecutorType;
import ca.waterloo.dsg.graphflow.query.executors.csp.DifferentialBFS;
import ca.waterloo.dsg.graphflow.query.executors.csp.LandmarkUnidirectionalUnweightedDifferentialBFS;
import ca.waterloo.dsg.graphflow.query.executors.csp.LandmarkUnidirectionalWeightedBaselineBFS;
import ca.waterloo.dsg.graphflow.query.executors.csp.LandmarkUnidirectionalWeightedDifferentialBFS;
import ca.waterloo.dsg.graphflow.query.operator.AbstractDBOperator;
import ca.waterloo.dsg.graphflow.util.Report;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zaidshennar on 2017-06-07.
 */
public abstract class ContinuousShortestPathPlan implements QueryPlan {
    static public boolean newBatch = true;
    public DifferentialBFS diffBFS;
    int queryId = -1;
    int source = -1;
    int destination = -1;
    AbstractDBOperator outputSink;
    List<Long> queryTime;
    ExecutorType executorType;


    public ContinuousShortestPathPlan(int queryId, int source, int destination, AbstractDBOperator outputSink) {
        this.queryId = queryId;
        this.source = source;
        this.destination = destination;
        this.outputSink = outputSink;
        queryTime = new LinkedList<Long>();
    }

    public int getSource() {
        return source;
    }

    public int getDestination() {
        return destination;
    }

    public AbstractDBOperator getOutputSink() {
        return outputSink;
    }

    public abstract long getSrcDstDistance();

    public long[] getQueryTimes() {
        Long[] queryTimeArray = new Long[1];
        queryTimeArray = queryTime.toArray(queryTimeArray);
        long[] longQueryTimeArray = new long[queryTimeArray.length];

        for (int i = 0; i < queryTimeArray.length; i++) {
            longQueryTimeArray[i] = queryTimeArray[i].longValue();
        }

        //return queryTimeArray;
        return longQueryTimeArray;
    }

    public void printDiffs() {
        diffBFS.printDiffs();
    }

    public void printStats() {
        diffBFS.printStats();
    }

    public void printDiffs(Report.Level l) {
        diffBFS.printDiffs(l);
    }

    public int[] getSizeOfLandmarkDistances() {
        int[] result = new int[0];

        switch (executorType){
            case LANDMARK_DIFF:
                return ((LandmarkUnidirectionalUnweightedDifferentialBFS) diffBFS).sizeOfLandmarkDistances();
            case LANDMARK_W_DIFF:
                return ((LandmarkUnidirectionalWeightedDifferentialBFS) diffBFS).sizeOfLandmarkDistances();
            case LANDMARK_W_SPSP:
                return LandmarkUnidirectionalWeightedBaselineBFS.getInstance().sizeOfLandmarkDistances();
            default:
                return result;
        }
    }

    public abstract void execute(int batch_number);

    public void initializeStats() {
        if (diffBFS != null) {
            diffBFS.initRecalculateNumbers();
        }
    }
}
