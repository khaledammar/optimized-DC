package ca.waterloo.dsg.graphflow.query.plans;

import ca.waterloo.dsg.graphflow.query.executors.csp.DifferentialBFS;
import ca.waterloo.dsg.graphflow.query.operator.AbstractDBOperator;
import ca.waterloo.dsg.graphflow.util.Report;

import java.util.LinkedList;
import java.util.List;

public abstract class ContinuousPageRankPlan implements QueryPlan {
    static public boolean newBatch = true;
    public DifferentialBFS diffBFS;
    int queryId = -1;
    AbstractDBOperator outputSink;
    List<Long> queryTime;


    public ContinuousPageRankPlan(int queryId, AbstractDBOperator outputSink) {
        this.queryId = queryId;
        this.outputSink = outputSink;
        queryTime = new LinkedList<Long>();
    }

    public AbstractDBOperator getOutputSink() {
        return outputSink;
    }

    public abstract int getPR(int vertexId);

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


    public abstract void execute();

    public void initializeStats() {
        if (diffBFS != null) {
            diffBFS.initRecalculateNumbers();
        }
    }
}
