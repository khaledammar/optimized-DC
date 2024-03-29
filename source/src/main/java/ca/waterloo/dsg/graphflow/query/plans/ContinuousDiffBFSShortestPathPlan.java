package ca.waterloo.dsg.graphflow.query.plans;

import ca.waterloo.dsg.graphflow.ExecutorType;
import ca.waterloo.dsg.graphflow.graph.Graph.Direction;
import ca.waterloo.dsg.graphflow.query.executors.csp.*;
import ca.waterloo.dsg.graphflow.query.operator.AbstractDBOperator;
import ca.waterloo.dsg.graphflow.util.Report;
import ca.waterloo.dsg.graphflow.util.Timer;

import java.util.Map;

/**
 * Class representing plan for a CONTINUOUS SHORTEST PATH operation
 */
public class ContinuousDiffBFSShortestPathPlan extends ContinuousShortestPathPlan {

    public DifferentialBFS initialDiffBFS;
    boolean backtrack;
    float dropProbability;
    DistancesWithDropBloom.DropType dropType;
    String bloomType;
    int minimumDegree, maxDegree;
    int landmarkNumber;


    public ContinuousDiffBFSShortestPathPlan(int queryId, int source, int destination, AbstractDBOperator outputSink,
                                             ExecutorType executorType, boolean backtrack, float dropProbability,
                                             DistancesWithDropBloom.DropType dropType, String bloomType, int minimumDegree,
                                             int maxDegree, int landmarkNumber) {
        super(queryId, source, destination, outputSink);

        this.backtrack = backtrack;
        this.dropProbability = dropProbability;
        this.dropType = dropType;
        this.bloomType = bloomType;
        this.minimumDegree = minimumDegree;
        this.maxDegree = maxDegree;
        this.landmarkNumber = landmarkNumber;
        this.executorType = executorType;

        newBatch = true;
        setDiffBFS();

        if (initialDiffBFS != null) {
            //Report.INSTANCE.debug("**** this.initialDiffBFS.continueBFS();  query=" + queryId);
            this.initialDiffBFS.continueBFS();
            //Report.INSTANCE.debug("**** this.initialDiffBFS.mergeDeltaDiff();");
            this.initialDiffBFS.mergeDeltaDiff();
            //Report.INSTANCE.debug("**** this.diffBFS.copyDiffs(initialDiffBFS);");
            this.diffBFS.copyDiffs(initialDiffBFS);
            this.initialDiffBFS.printDiffs();
        } else {
            //Report.INSTANCE.debug("**** this.diffBFS.continueBFS();");
            this.diffBFS.continueBFS();
            this.diffBFS.mergeDeltaDiff();
            Report.INSTANCE.debug("**** Initial computation results ****");
            this.diffBFS.printDiffs();
            Report.INSTANCE.debug("**** Loaded ****");
        }
    }

    public ContinuousDiffBFSShortestPathPlan(int queryId, int source, int destination, AbstractDBOperator outputSink,
                                             ExecutorType executorType, boolean backtrack) {
        super(queryId, source, destination, outputSink);

        newBatch = true;
        this.backtrack = backtrack;
        setDiffBFS();
        diffBFS.continueBFS();
    }


    public void initializeDiff() {
        diffBFS.initRecalculateNumbers();
    }

    private void setDiffBFS() {

        //Report.INSTANCE.debug(" ** ContinuousDiffBFSShortestPathPlan Constructor ** " + executorType);

        switch (executorType) {

            case PR_DC:
                this.diffBFS = new NewDifferentialPRDC(queryId, source, destination, Direction.FORWARD);
                break;
            case PR_CDD:
                this.diffBFS = new NewDifferentialPR(queryId, destination, Direction.FORWARD, backtrack,
                        NewUnidirectionalDifferentialBFS.Queries.PR);
                this.initialDiffBFS = null;
                break;
            case PR_CDD_PROB:
                this.diffBFS = new NewDifferentialPR(queryId, destination, Direction.FORWARD, backtrack,
                        NewUnidirectionalDifferentialBFS.DropIndex.BLOOM, dropProbability, dropType, bloomType,
                        minimumDegree, maxDegree, NewUnidirectionalDifferentialBFS.Queries.PR);
                this.initialDiffBFS = new NewDifferentialPR(0, destination, Direction.FORWARD, backtrack,
                        NewUnidirectionalDifferentialBFS.Queries.PR);
                break;
            case PR_CDD_DET:
                this.diffBFS = new NewDifferentialPR(queryId, destination, Direction.FORWARD, backtrack,
                        NewUnidirectionalDifferentialBFS.DropIndex.HASH_TABLE, dropProbability, dropType, bloomType,
                        minimumDegree, maxDegree, NewUnidirectionalDifferentialBFS.Queries.PR);
                this.initialDiffBFS = new NewDifferentialPR(0, destination, Direction.FORWARD, backtrack,
                        NewUnidirectionalDifferentialBFS.Queries.PR);
                break;

            case WCC_DC:
                this.diffBFS = new NewDifferentialWCCDC(queryId, source, destination, Direction.FORWARD);
                break;
            case WCC_CDD:
                this.diffBFS = new NewDifferentialWCC(queryId, destination, Direction.FORWARD, backtrack,
                        NewUnidirectionalDifferentialBFS.Queries.WCC);
                this.initialDiffBFS = null;
                break;
            case WCC_CDD_PROB:
                this.diffBFS = new NewDifferentialWCC(queryId, destination, Direction.FORWARD, backtrack,
                        NewUnidirectionalDifferentialBFS.DropIndex.BLOOM, dropProbability, dropType, bloomType,
                        minimumDegree, maxDegree, NewUnidirectionalDifferentialBFS.Queries.WCC);
                this.initialDiffBFS = new NewDifferentialWCC(0, destination, Direction.FORWARD, backtrack,
                        NewUnidirectionalDifferentialBFS.Queries.WCC);
                break;
            case WCC_CDD_DET:
                this.diffBFS = new NewDifferentialWCC(queryId, destination, Direction.FORWARD, backtrack,
                        NewUnidirectionalDifferentialBFS.DropIndex.HASH_TABLE, dropProbability, dropType, bloomType,
                        minimumDegree, maxDegree, NewUnidirectionalDifferentialBFS.Queries.WCC);
                this.initialDiffBFS = new NewDifferentialWCC(0, destination, Direction.FORWARD, backtrack,
                        NewUnidirectionalDifferentialBFS.Queries.WCC);
                break;


            case SPSP_W_DC:
                this.diffBFS = new NewDifferentialSPSPDC(queryId, source, destination, Direction.FORWARD);
                break;
            case SPSP_W_DC_JOD:
                this.diffBFS = new NewDifferentialSPSPDCJOD(queryId, source, destination);
                break;
            case SPSP_W_CDD:
                this.diffBFS = new NewUnidirectionalWeightedDifferentialBFS(queryId, source, destination, backtrack,
                        NewUnidirectionalDifferentialBFS.DropIndex.NO_DROP,
                        NewUnidirectionalDifferentialBFS.Queries.SPSP);
                this.initialDiffBFS = null;
                break;
            case SPSP_W_CDD_PROB:
                this.diffBFS = new NewUnidirectionalWeightedDifferentialBFS(queryId, source, destination, backtrack,
                        NewUnidirectionalDifferentialBFS.DropIndex.BLOOM /* use FAKE diffs*/, dropProbability, dropType,
                        bloomType, minimumDegree, maxDegree, NewUnidirectionalDifferentialBFS.Queries.SPSP);
                this.initialDiffBFS = new NewUnidirectionalWeightedDifferentialBFS(0, source, destination, backtrack,
                        NewUnidirectionalDifferentialBFS.DropIndex.NO_DROP,
                        NewUnidirectionalDifferentialBFS.Queries.SPSP);
                break;
            case SPSP_W_CDD_DET:
                this.diffBFS = new NewUnidirectionalWeightedDifferentialBFS(queryId, source, destination, backtrack,
                        NewUnidirectionalDifferentialBFS.DropIndex.HASH_TABLE /* use FAKE diffs*/, dropProbability,
                        dropType, bloomType, minimumDegree, maxDegree, NewUnidirectionalDifferentialBFS.Queries.SPSP);
                this.initialDiffBFS = new NewUnidirectionalWeightedDifferentialBFS(0, source, destination, backtrack,
                        NewUnidirectionalDifferentialBFS.DropIndex.NO_DROP,
                        NewUnidirectionalDifferentialBFS.Queries.SPSP);
                break;

            case KHOP_DC:
                this.diffBFS = new NewDifferentialKHOPDC(queryId, source, destination, Direction.FORWARD);
                break;
            case KHOP_CDD:
                this.diffBFS = new NewDifferentialKHOP(queryId, source, destination, Direction.FORWARD, backtrack,
                        NewUnidirectionalDifferentialBFS.Queries.KHOP);
                this.initialDiffBFS = null;
                break;
            case KHOP_CDD_PROB:
                this.diffBFS = new NewDifferentialKHOP(queryId, source, destination, Direction.FORWARD, backtrack,
                        NewUnidirectionalDifferentialBFS.DropIndex.BLOOM, dropProbability, dropType, bloomType,
                        minimumDegree, maxDegree, NewUnidirectionalDifferentialBFS.Queries.KHOP);
                this.initialDiffBFS = new NewDifferentialKHOP(0, source, destination, Direction.FORWARD, backtrack,
                        NewUnidirectionalDifferentialBFS.Queries.KHOP);
                break;
            case KHOP_CDD_DET:
                this.diffBFS = new NewDifferentialKHOP(queryId, source, destination, Direction.FORWARD, backtrack,
                        NewUnidirectionalDifferentialBFS.DropIndex.HASH_TABLE, dropProbability, dropType, bloomType,
                        minimumDegree, maxDegree, NewUnidirectionalDifferentialBFS.Queries.KHOP);
                this.initialDiffBFS = new NewDifferentialKHOP(0, source, destination, Direction.FORWARD, backtrack,
                        NewUnidirectionalDifferentialBFS.Queries.KHOP);
                break;

            case Q1_DC:
                this.diffBFS = new NewDifferentialQ1DC(queryId, source, Direction.FORWARD);
                break;
            case Q1_CDD:
                this.diffBFS = new NewDifferentialQ1(queryId, source, Direction.FORWARD, backtrack,
                        NewUnidirectionalDifferentialBFS.Queries.Q1);
                this.initialDiffBFS = null;
                break;
            case Q1_CDD_PROB:
                this.diffBFS = new NewDifferentialQ1(queryId, source, Direction.FORWARD, backtrack,
                        NewUnidirectionalDifferentialBFS.DropIndex.BLOOM, dropProbability, dropType, bloomType,
                        minimumDegree, maxDegree, NewUnidirectionalDifferentialBFS.Queries.Q1);
                this.initialDiffBFS = new NewDifferentialQ1(0, source, Direction.FORWARD, backtrack,
                        NewUnidirectionalDifferentialBFS.Queries.Q1);
                break;
            case Q1_CDD_DET:
                this.diffBFS = new NewDifferentialQ1(queryId, source, Direction.FORWARD, backtrack,
                        NewUnidirectionalDifferentialBFS.DropIndex.HASH_TABLE, dropProbability, dropType, bloomType,
                        minimumDegree, maxDegree, NewUnidirectionalDifferentialBFS.Queries.Q1);
                this.initialDiffBFS = new NewDifferentialQ1(0, source, Direction.FORWARD, backtrack,
                        NewUnidirectionalDifferentialBFS.Queries.Q1);
                break;

            case Q2_DC:
                this.diffBFS = new NewDifferentialQ2DC(queryId, source, Direction.FORWARD);
                break;
            case Q2_CDD:
                this.diffBFS = new NewDifferentialQ2(queryId, source, Direction.FORWARD, backtrack,
                        NewUnidirectionalDifferentialBFS.Queries.Q2);
                this.initialDiffBFS = null;
                break;
            case Q2_CDD_PROB:
                this.diffBFS = new NewDifferentialQ2(queryId, source, Direction.FORWARD, backtrack,
                        NewUnidirectionalDifferentialBFS.DropIndex.BLOOM, dropProbability, dropType, bloomType,
                        minimumDegree, maxDegree, NewUnidirectionalDifferentialBFS.Queries.Q2);
                this.initialDiffBFS = new NewDifferentialQ2(0, source, Direction.FORWARD, backtrack,
                        NewUnidirectionalDifferentialBFS.Queries.Q2);
                break;
            case Q2_CDD_DET:
                this.diffBFS = new NewDifferentialQ2(queryId, source, Direction.FORWARD, backtrack,
                        NewUnidirectionalDifferentialBFS.DropIndex.HASH_TABLE, dropProbability, dropType, bloomType,
                        minimumDegree, maxDegree, NewUnidirectionalDifferentialBFS.Queries.Q2);
                this.initialDiffBFS = new NewDifferentialQ2(0, source, Direction.FORWARD, backtrack,
                        NewUnidirectionalDifferentialBFS.Queries.Q2);
                break;

            /**
             * Q7 RPQ query: A.B.C*  = likes . hasCreator . knows
             *
             *
             */
            case Q7_DC:
                this.diffBFS = new NewDifferentialQ7DC(queryId, source, Direction.FORWARD);
                break;

            case Q7_CDD:
                this.diffBFS = new NewDifferentialQ7(queryId, source, Direction.FORWARD, backtrack,
                        NewUnidirectionalDifferentialBFS.Queries.Q7);
                this.initialDiffBFS = null;
                break;
            case Q7_CDD_PROB:
                this.diffBFS = new NewDifferentialQ7(queryId, source, Direction.FORWARD, backtrack,
                        NewUnidirectionalDifferentialBFS.DropIndex.BLOOM, dropProbability, dropType, bloomType,
                        minimumDegree, maxDegree, NewUnidirectionalDifferentialBFS.Queries.Q7);
                this.initialDiffBFS = new NewDifferentialQ7(0, source, Direction.FORWARD, backtrack,
                        NewUnidirectionalDifferentialBFS.Queries.Q7);
                break;
            case Q7_CDD_DET:
                this.diffBFS = new NewDifferentialQ7(queryId, source, Direction.FORWARD, backtrack,
                        NewUnidirectionalDifferentialBFS.DropIndex.HASH_TABLE, dropProbability, dropType, bloomType,
                        minimumDegree, maxDegree, NewUnidirectionalDifferentialBFS.Queries.Q7);
                this.initialDiffBFS = new NewDifferentialQ7(0, source, Direction.FORWARD, backtrack,
                        NewUnidirectionalDifferentialBFS.Queries.Q7);
                break;

            /**
             * * Q11: A1.A2.A3. .... Ak
             * * Q11: likes. replyOf. hasCreator.knows. likes
             *
             *
             */
            case Q11_DC:
                this.diffBFS = new NewDifferentialQ11DC(queryId, source, Direction.FORWARD);
                break;

            case Q11_CDD:
                this.diffBFS = new NewDifferentialQ11(queryId, source, Direction.FORWARD, backtrack,
                        NewUnidirectionalDifferentialBFS.Queries.Q11);
                this.initialDiffBFS = null;
                break;
            case Q11_CDD_PROB:
                this.diffBFS = new NewDifferentialQ11(queryId, source, Direction.FORWARD, backtrack,
                        NewUnidirectionalDifferentialBFS.DropIndex.BLOOM, dropProbability, dropType, bloomType,
                        minimumDegree, maxDegree, NewUnidirectionalDifferentialBFS.Queries.Q11);
                this.initialDiffBFS = new NewDifferentialQ11(0, source, Direction.FORWARD, backtrack,
                        NewUnidirectionalDifferentialBFS.Queries.Q11);
                break;
            case Q11_CDD_DET:
                this.diffBFS = new NewDifferentialQ11(queryId, source, Direction.FORWARD, backtrack,
                        NewUnidirectionalDifferentialBFS.DropIndex.HASH_TABLE, dropProbability, dropType, bloomType,
                        minimumDegree, maxDegree, NewUnidirectionalDifferentialBFS.Queries.Q11);
                this.initialDiffBFS = new NewDifferentialQ11(0, source, Direction.FORWARD, backtrack,
                        NewUnidirectionalDifferentialBFS.Queries.Q11);
                break;
            /**
             * New algorithm for one direction, Unweighted BFS
             */
            case UNI_UNWEIGHTED_DIFF_BFS:
                this.diffBFS =
                        new NewUnidirectionalUnweightedDifferentialBFS(queryId, source, destination, Direction.FORWARD,
                                backtrack, NewUnidirectionalDifferentialBFS.Queries.KHOP);
                this.initialDiffBFS = null;
                break;

            case UNI_UNWEIGHTED_DIFF_BFS_BLOOM:
                this.diffBFS =
                        new NewUnidirectionalUnweightedDifferentialBFS(queryId, source, destination, Direction.FORWARD,
                                backtrack, NewUnidirectionalDifferentialBFS.DropIndex.BLOOM, dropProbability, dropType,
                                bloomType, minimumDegree, maxDegree, NewUnidirectionalDifferentialBFS.Queries.KHOP);
                this.initialDiffBFS =
                        new NewUnidirectionalUnweightedDifferentialBFS(0, source, destination, Direction.FORWARD,
                                backtrack, NewUnidirectionalDifferentialBFS.Queries.KHOP);
                break;

            case UNI_UNWEIGHTED_DIFF_BFS_HASH:
                this.diffBFS =
                        new NewUnidirectionalUnweightedDifferentialBFS(queryId, source, destination, Direction.FORWARD,
                                backtrack, NewUnidirectionalDifferentialBFS.DropIndex.HASH_TABLE, dropProbability,
                                dropType, bloomType, minimumDegree, maxDegree,
                                NewUnidirectionalDifferentialBFS.Queries.KHOP);
                this.initialDiffBFS =
                        new NewUnidirectionalUnweightedDifferentialBFS(0, source, destination, Direction.FORWARD,
                                backtrack, NewUnidirectionalDifferentialBFS.Queries.KHOP);
                break;

            /**
             * New algorithm for one direction, Weighted BFS with dropping Random vertices
             */
            case UNIDIR_WEIGHTED_DIFF_BFS_DROP_RANDOM_VERTEX:
                this.diffBFS =
                        new NewUnidirectionalWeightedDifferentialBFSDropRandomVertexDiff(queryId, source, destination,
                                backtrack, dropProbability, NewUnidirectionalDifferentialBFS.Queries.SPSP);
                this.initialDiffBFS = null;
                break;

            /**
             * New algorithm for one direction, W+ BFS
             */

            case UNIDIR_POSITIVE_WEIGHTED_DIFF_BFS:
                this.diffBFS = new NewWeightedDifferentialBFSWithPositiveEdges(queryId, source, destination, backtrack,
                        NewUnidirectionalDifferentialBFS.Queries.SPSP);
                this.initialDiffBFS = null;
                break;

            case OPTIMIZED_BIDIR_UNWEIGHTED_DIFF_BFS:
                this.diffBFS = new OptimizedBidirUnweightedDifferentialBFS(queryId, source, destination, backtrack,
                        NewUnidirectionalDifferentialBFS.Queries.SPSP);
                this.initialDiffBFS = null;
                break;

            // assume Forward direction for now!
            case LANDMARK_DIFF:
                this.diffBFS = new LandmarkUnidirectionalUnweightedDifferentialBFS(queryId, source, destination,
                        Direction.FORWARD, backtrack, landmarkNumber, NewUnidirectionalDifferentialBFS.Queries.Landmark_SPSP);
                this.initialDiffBFS = null;
                break;

            case LANDMARK_W_DIFF:
                this.diffBFS = new LandmarkUnidirectionalWeightedDifferentialBFS(queryId, source, destination,
                        Direction.FORWARD, backtrack, landmarkNumber, NewUnidirectionalDifferentialBFS.Queries.Landmark_W_SPSP);
                this.initialDiffBFS = null;
                break;

            default:
                throw new RuntimeException(" **** Error: Execution type is not defined " + executorType);
                //this.diffBFS = new NewUnidirectionalUnweightedDifferentialBFS(source, destination,
                //    Direction.FORWARD, backtrack);

        }
    }


    /**
     * Executes the Unidirectional Differential-BFS
     */
    public void execute(int batch_number) {
/*
        if (Report.INSTANCE.appReportingLevel == Report.Level.DEBUG) {
            Report.INSTANCE.debug(" ***************************************************");
            Report.INSTANCE.debug(" ***************************************************");
            Report.INSTANCE.debug(" ** Execute in ContinuousDiffBFSShortestPathPlan **");
        }

 */
        long startTime, endTime;
        if (ExecutorType.isLandmark(executorType)){
            //System.out.println("=====================================");
            startTime = System.nanoTime();
            diffBFS.preProcessing(batch_number);
            endTime = System.nanoTime();
            newBatch = false;
            //System.out.println("Landmark Index Time = "+ Timer.elapsedNanoToMilliString(endTime-startTime));
            //System.out.println("=====================================");
        }

        //System.out.println("=====================================");
        //System.out.println("=====================================");
        startTime = System.nanoTime();
        diffBFS.executeDifferentialBFS();
        endTime = System.nanoTime();

        //Report.INSTANCE.error("Query "+source+"-"+destination+" "+(endTime - startTime));
        if (Report.INSTANCE.appReportingLevel == Report.Level.INFO) {
            queryTime.add(endTime - startTime);
            diffBFS.printDiffs();
        }
    }

    public int getRecalculateNumbers() {
        return diffBFS.getRecalculateNumbers();
    }

    public int getSetVertexChangeNumbers() {
        return diffBFS.getSetVertexChangeNumbers();
    }



    public Map getRecalculateStat(){
        return diffBFS.getRecalculateStats();
    }

    public int getMaxIteration(){
        return diffBFS.getMaxIteration();
    }
    public int getSizeOfDistances() {
        return diffBFS.sizeOfDistances();
    }

    public int getNumberOfVertices() {
        return diffBFS.getNumberOfVertices();
    }

    public int getMinimumSizeOfDistances() {
        return diffBFS.minimumSizeOfDistances();
    }


    public int getSource() {
        return source;
    }

    public int getDest() {
        return destination;
    }

    public long getSrcDstDistance() {
        return diffBFS.getSrcDstDistance();
    }
}
