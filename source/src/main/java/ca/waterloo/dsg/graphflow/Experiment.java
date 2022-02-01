package ca.waterloo.dsg.graphflow;

import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.graph.TypeAndPropertyKeyStore;
import ca.waterloo.dsg.graphflow.query.executors.csp.*;
import ca.waterloo.dsg.graphflow.query.parser.StructuredQueryParser;
import ca.waterloo.dsg.graphflow.query.planner.ContinuousShortestPathPlanner;
import ca.waterloo.dsg.graphflow.query.plans.ContinuousDiffBFSShortestPathPlan;
import ca.waterloo.dsg.graphflow.query.plans.ContinuousShortestPathPlan;
import ca.waterloo.dsg.graphflow.query.structuredquery.StructuredQuery;
import ca.waterloo.dsg.graphflow.util.Report;
import ca.waterloo.dsg.graphflow.util.Timer;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import org.antlr.v4.runtime.misc.Pair;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.util.*;

import static ca.waterloo.dsg.graphflow.ExecutorType.*;


/**
 * The experiment for ContinuousShortestPath Query different implementations
 */
public class Experiment {

    public enum BatchType {
        ADDITION,
        DELETION
    }

    //
    //This is the base directory for dataset files. I need to change it to be a parameter.
    // The default is set to either khaled's or Semih's machine for easy debug.
    //
    //From Semih's Machine:
    //private static String DEFAULT_ARG_BASE_DIR = "/Users/semihsalihoglu/Desktop/research/waterloo/graphflow/github/datasets/";
    private static final String ARG_NEW_FILE_FORMAT = "useNewFormat";
    private static final String ARG_TIMERS = "useTimers";
    private static final String SPLIT_REGEX = "\t+|\\s+|,";
    private static final long MEGABYTE = 1024L * 1024L;
    private static String ARG_BASE_DIR = "baseDir";
    //From Khaled's machine
    private static String DEFAULT_ARG_BASE_DIR = "/Users/U6035886/Uwaterloo/semih-new-project/dataset/";
    private static String ARG_GRAPH_FILE_TO_LOAD = "graphFileToLoad";
    private static String DEFAULT_ARG_GRAPH_FILE_TO_LOAD = "soc-Epinions1-90.txt";
    private static String ARG_CONNECTED_QUERY_FILE = "connQueryFile";
    private static String DEFAULT_ARG_CONNECTED_QUERY_FILE = "soc-Epinions1-90-1000-connected-queries.txt";
    private static String ARG_DISCONNECTED_QUERY_FILE = "disconnQueryFile";
    private static String DEFAULT_ARG_DISCONNECTED_QUERY_FILE = "soc-Epinions1-90-1000-disconnected-queries.txt";
    private static String ARG_ADD_EDGES_FILE = "addEdgesFile";
    private static String DEFAULT_ARG_ADD_EDGES_FILE = "soc-Epinions1-10.txt";
    private static String ARG_DELETE_EDGES_FILE = "deleteEdgesFile";
    private static String DEFAULT_ARG_DELETE_EDGES_FILE = "soc-Epinions1-90-del.txt";
    private static String ARG_EDIT_EDGES_FILE = "editEdgesFile";
    private static String DEFAULT_ARG_EDIT_EDGES_FILE = "EP_edit.txt";
    private static String ARG_DELETION_PROBABILITY = "deletionProbability";
    private static String DEFAULT_ARG_DELETION_PROBABILITY = "0.0";
    private static String ARG_BATCH_SIZE = "batchSize";
    private static String DEFAULT_ARG_BATCH_SIZE = "256";
    private static String ARG_NUM_QUERIES = "numQueries";
    private static String DEFAULT_ARG_NUM_QUERIES = "1";
    private static String ARG_CONNECTED_QUERY_PERCENTAGE = "connectedQueryPercentage";
    private static String DEFAULT_ARG_CONNECTED_QUERY_PERCENTAGE = "1.0";
    private static String ARG_NUM_BATCHES = "numBatches";
    private static String DEFAULT_ARG_NUM_BATCHES = "10000";
    private static String ARG_EXECUTOR_TYPE = "executorType";
    private static String DEFAULT_ARG_EXECUTOR_TYPE = "baseline";
    private static String ARG_SEED_ADD_DELETE = "addDeleteSeed";
    private static String DEFAULT_ARG_SEED_ADD_DELETE = "2457";
    private static String ARG_SEED_SRC_DST = "srcDestSeed";
    private static String DEFAULT_ARG_SEED_SRC_DST = "5592";
    private static String ARG_SEED_EDGE_WEIGHT = GenerateWeightedGraphs.ARG_SEED_EDGE_WEIGHT;
    private static String DEFAULT_ARG_SEED_EDGE_WEIGHT = GenerateWeightedGraphs.DEFAULT_ARG_SEED_EDGE_WEIGHT;
    private static String ARG_SEED_EDGE_GEN = "edgeGenSeed";
    private static String DEFAULT_ARG_SEED_EDGE_GEN = "124521";
    private static String ARG_EDGE_WEIGHT_RANGE = GenerateWeightedGraphs.ARG_EDGE_WEIGHT_RANGE;
    private static String DEFAULT_ARG_EDGE_WEIGHT_RANGE = GenerateWeightedGraphs.DEFAULT_ARG_EDGE_WEIGHT_RANGE;
    private static String ARG_BACKTRACK = "backtrack";
    private static String DEFAULT_ARG_BACKTRACK = "false";
    private static String ARG_PRINT_DISTANCES = "printdistances";
    private static String DEFAULT_ARG_PRINT_DISTANCES = "false";
    private static String ARG_REPORT_LEVEL = "reportingLevel";
    private static String DEFAULT_ARG_REPORT_LEVEL = "Error";
    private static String ARG_LANDMARK_NUMBER = "LandmarkNumber";
    private static String DEFAULT_ARG_LANDMARK_NUMBER = "1";
    private static String ARG_DROP_PROBABILITY = "DropProbability";
    private static String DEFAULT_ARG_DROP_PROBABILITY = "0";
    private static String ARG_DROP_TYPE = "DropType";
    private static String DEFAULT_ARG_DROP_TYPE = "Random";
    private static String ARG_BLOOM_TYPE = "BloomType";
    private static String DEFAULT_ARG_BLOOM_TYPE = "Query";
    private static String ARG_DROP_MINIMUM = "DropMinimum";
    private static String DEFAULT_ARG_DROP_MINIMUM = "2";
    private static String ARG_DROP_MAXIMUM = "DropMaximum";
    private static String DEFAULT_ARG_DROP_MAXIMUM = "10";
    // This parameter is only relevent for weighted graphs
    // It decides if we should edit the weights or delete/add edges
    private static String ARG_UPDATE_OR_DELETEADD = "edit_or_deleteadd";
    private static String DEFAULT_ARG_UPDATE_OR_DELETEADD = "deleteadd";
    private static int DEFAULT_ARG_PREPARATION_QUERIES = 10;
    private final boolean useNewFormat;
    private Random addDeleteRand;
    private Random srcDestRand;
    private GenerateWeightedGraphs edgeWeightGenerator;
    private String baseDir = "";
    private String graphFileToLoad = "";
    private String connectedQueryFile = "";
    private String disconnectedQueryFile = "";
    private double connectedQueryPercentage;
    private String addEdgesFile = "";
    private String editEdgesFile = "";
    private String delEdgesFile = "";
    private String updateAction = "";
    private double deletionProbability;
    private ExecutorType executorType;
    private int landmarkNumber;
    private int numQueries;
    private int numBatches;
    private int batchSize;
    private float dropProbability;
    private DistancesWithDropBloom.DropType dropType;
    private String bloomType;
    private int dropMinimum, dropMaximum;
    // Update queries!
    private List<EdgeBatch> batchData = new ArrayList<>();
    List<Map<Integer, Map<Integer, DistancesDC.Diff2>>> batchDiffs = new ArrayList<>();
    private List<Long> batchTimes = new LinkedList<>();
    private List<List<Long>> srcDstDistances = new LinkedList<>();
    private boolean isWeighted = false;
    private short vertexTypeId;
    private short edgeTypeId;
    private int edgeWeightSeed;
    private int edgeWeightRange;
    private boolean backtrack;
    private boolean printDistances;

    public Experiment(String baseDir, String graphFileToLoad, boolean useNewFormat, String connectedQueryFile,
                      String disconnectedQueryFile, String addEdgesFile, String delEdgesFile, String editEdgesFile,
                      double deletionProbability, ExecutorType executorType, int numQueries,
                      double connectedQueryPercentage, int numBatches, int batchSize, int addDeleteSeed,
                      int srcDestSeed, int edgeWeightSeed, boolean isWeighted, int edgeWeightRange, boolean backtrack,
                      boolean printDistances, String updateAction, int landmarkNumber, float dropProbability,
                      DistancesWithDropBloom.DropType dropType, String bloomType, int dropMinimum, int dropMaximum) {
        this.baseDir = baseDir;
        this.graphFileToLoad = graphFileToLoad;
        this.useNewFormat = useNewFormat;
        this.connectedQueryFile = connectedQueryFile;
        this.disconnectedQueryFile = disconnectedQueryFile;
        this.addEdgesFile = addEdgesFile;
        this.editEdgesFile = editEdgesFile;
        this.delEdgesFile = delEdgesFile;
        this.deletionProbability = deletionProbability;
        this.executorType = executorType;
        this.numQueries = numQueries;
        this.connectedQueryPercentage = connectedQueryPercentage;
        this.numBatches = numBatches;
        this.batchSize = batchSize;
        this.addDeleteRand = new Random(addDeleteSeed);
        this.srcDestRand = new Random(srcDestSeed);
        this.edgeWeightSeed = edgeWeightSeed;
        this.edgeWeightRange = edgeWeightRange;
        this.updateAction = updateAction;
        this.landmarkNumber = landmarkNumber;
        this.dropProbability = dropProbability;
        this.dropType = dropType;
        this.bloomType = bloomType;
        this.dropMinimum = dropMinimum;
        this.dropMaximum = dropMaximum;

        this.isWeighted = isWeighted;
        if (isWeighted) {
            Graph.IS_WEIGHTED = true;
        }

        this.edgeWeightGenerator =
                new GenerateWeightedGraphs(this.edgeWeightSeed, this.edgeWeightRange, getWeightSign(this.executorType));

        this.backtrack = backtrack;
        this.printDistances = printDistances;
        vertexTypeId = TypeAndPropertyKeyStore.getInstance().mapStringTypeToShortOrInsert("VERTEX");
        edgeTypeId = TypeAndPropertyKeyStore.getInstance().mapStringTypeToShortOrInsert("WEIGHT");
    }

    private static long bytesToMegabytes(long bytes) {
        return bytes / MEGABYTE;
    }

    private static long getMBmemory() {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        runtime.gc();
        long memory = runtime.totalMemory() - runtime.freeMemory();
        return bytesToMegabytes(memory);
    }

    /**
     * This is the main function of the experiments
     *
     * @param args
     * @throws ParseException
     * @throws NumberFormatException
     * @throws IOException
     */
    public static void main(String[] args) throws ParseException, NumberFormatException, IOException {

        System.out.println("Memory at the beginning " + getMBmemory());
        // Parse command line options
        CommandLine cmd = new DefaultParser().parse(getCommandLineOptions(), args);

        String baseDir = cmd.getOptionValue(ARG_BASE_DIR, DEFAULT_ARG_BASE_DIR);
        String graphFileToLoad = cmd.getOptionValue(ARG_GRAPH_FILE_TO_LOAD, DEFAULT_ARG_GRAPH_FILE_TO_LOAD);
        boolean useNewFormat = Boolean.parseBoolean(cmd.getOptionValue(ARG_NEW_FILE_FORMAT, "false"));
        String connectedQueryFile = cmd.getOptionValue(ARG_CONNECTED_QUERY_FILE, DEFAULT_ARG_CONNECTED_QUERY_FILE);
        String disconnectedQueryFile =
                cmd.getOptionValue(ARG_DISCONNECTED_QUERY_FILE, DEFAULT_ARG_DISCONNECTED_QUERY_FILE);
        String addEdgesFile = cmd.getOptionValue(ARG_ADD_EDGES_FILE, DEFAULT_ARG_ADD_EDGES_FILE);
        String editEdgesFile = cmd.getOptionValue(ARG_EDIT_EDGES_FILE, DEFAULT_ARG_EDIT_EDGES_FILE);
        String delEdgesFile = cmd.getOptionValue(ARG_DELETE_EDGES_FILE, DEFAULT_ARG_DELETE_EDGES_FILE);
        double deletionProbability =
                Double.parseDouble(cmd.getOptionValue(ARG_DELETION_PROBABILITY, DEFAULT_ARG_DELETION_PROBABILITY));
        int batchSize = Integer.parseInt(cmd.getOptionValue(ARG_BATCH_SIZE, DEFAULT_ARG_BATCH_SIZE));
        int numBatches = Integer.parseInt(cmd.getOptionValue(ARG_NUM_BATCHES, DEFAULT_ARG_NUM_BATCHES));
        int numQueries = Integer.parseInt(cmd.getOptionValue(ARG_NUM_QUERIES, DEFAULT_ARG_NUM_QUERIES));
        double connectedQueryPercentage = Double.parseDouble(
                cmd.getOptionValue(ARG_CONNECTED_QUERY_PERCENTAGE, DEFAULT_ARG_CONNECTED_QUERY_PERCENTAGE));
        ExecutorType executorType =
                ExecutorType.getFromCommandLineName(cmd.getOptionValue(ARG_EXECUTOR_TYPE, DEFAULT_ARG_EXECUTOR_TYPE));
        // This also means that we introduce changes as an update to the weight instead of add/delete edges
        boolean isWeighted = ExecutorType.isWeighted(executorType);

        int addDeleteSeed = Integer.parseInt(cmd.getOptionValue(ARG_SEED_ADD_DELETE, DEFAULT_ARG_SEED_ADD_DELETE));
        int srcDestSeed = Integer.parseInt(cmd.getOptionValue(ARG_SEED_SRC_DST, DEFAULT_ARG_SEED_SRC_DST));
        int edgeWeightSeed = Integer.parseInt(cmd.getOptionValue(ARG_SEED_EDGE_WEIGHT, DEFAULT_ARG_SEED_EDGE_WEIGHT));
        int edgeGenSeed = Integer.parseInt(cmd.getOptionValue(ARG_SEED_EDGE_GEN, DEFAULT_ARG_SEED_EDGE_GEN));
        int edgeWeightRange =
                Integer.parseInt(cmd.getOptionValue(ARG_EDGE_WEIGHT_RANGE, DEFAULT_ARG_EDGE_WEIGHT_RANGE));
        boolean backtrack = Boolean.parseBoolean(cmd.getOptionValue(ARG_BACKTRACK, DEFAULT_ARG_BACKTRACK));
        boolean printDistances =
                Boolean.parseBoolean(cmd.getOptionValue(ARG_PRINT_DISTANCES, DEFAULT_ARG_PRINT_DISTANCES));

        String reportLevelString = cmd.getOptionValue(ARG_REPORT_LEVEL, DEFAULT_ARG_REPORT_LEVEL);

        int landmarkNumber = Integer.parseInt(cmd.getOptionValue(ARG_LANDMARK_NUMBER, DEFAULT_ARG_LANDMARK_NUMBER));
        float dropProbability =
                Float.parseFloat(cmd.getOptionValue(ARG_DROP_PROBABILITY, DEFAULT_ARG_DROP_PROBABILITY));
        String dropTypeString = cmd.getOptionValue(ARG_DROP_TYPE, DEFAULT_ARG_DROP_TYPE);
        DistancesWithDropBloom.DropType dropType;
        switch (dropTypeString) {
            case "Random":
                dropType = DistancesWithDropBloom.DropType.RANDOM;
                break;
            case "Selective":
                dropType = DistancesWithDropBloom.DropType.SELECTIVE;
                break;
            default:
                throw new RuntimeException(dropTypeString + " not recognized");
        }
        String bloomType = cmd.getOptionValue(ARG_BLOOM_TYPE, DEFAULT_ARG_BLOOM_TYPE);
        int dropMinimum = Integer.parseInt(cmd.getOptionValue(ARG_DROP_MINIMUM, DEFAULT_ARG_DROP_MINIMUM));
        int dropMaximum = Integer.parseInt(cmd.getOptionValue(ARG_DROP_MAXIMUM, DEFAULT_ARG_DROP_MAXIMUM));

        String updateAction = cmd.getOptionValue(ARG_UPDATE_OR_DELETEADD, DEFAULT_ARG_UPDATE_OR_DELETEADD);

        if (!updateAction.equals("edit") && !updateAction.equals("deleteadd")) {
            // wrong parameter value
            System.out.println("Error: Parameter (" + ARG_UPDATE_OR_DELETEADD +
                    ") can be 'edit' or 'deleteadd'.\nThe given value was " + updateAction);
            return;
        }

        switch (reportLevelString) {
            case "Error":
                Report.INSTANCE.setLevel(Report.Level.ERROR);
                break;
            case "Debug":
                Report.INSTANCE.setLevel(Report.Level.DEBUG);
                break;
            case "Info":
                Report.INSTANCE.setLevel(Report.Level.INFO);
                break;
            default:
                System.out.println("ERROR: report level is not supported --> " + reportLevelString);
                Report.INSTANCE.setLevel(Report.Level.ERROR);
        }
        Graph.getInstance().setSeed(edgeGenSeed);

        // Start Timer
        long beginTime = System.nanoTime();

        // create a new experiment class
        Experiment experiment =
                new Experiment(baseDir, graphFileToLoad, useNewFormat, connectedQueryFile, disconnectedQueryFile,
                        addEdgesFile, delEdgesFile, editEdgesFile, deletionProbability, executorType, numQueries,
                        connectedQueryPercentage, numBatches, batchSize, addDeleteSeed, srcDestSeed, edgeWeightSeed,
                        isWeighted, edgeWeightRange, backtrack, printDistances, updateAction, landmarkNumber,
                        dropProbability, dropType, bloomType, dropMinimum, dropMaximum);

        System.out.println("Memory after initiating experiment " + getMBmemory());
        // run the experiment
        experiment.prepareAndExecuteExperiment(addDeleteSeed);
        long endTime = System.nanoTime();
        System.out.println(experiment);
        System.out.println("Memory at the end of experiment " + getMBmemory());
        System.out
                .println("Total time the entire experiment took: " + (endTime - beginTime) / 1000000000.0 + " seconds");
    }

    private static Options getCommandLineOptions() {
        Options options = new Options();
        options.addOption("help", false, "print this message");
        options.addOption(ARG_BASE_DIR, ARG_BASE_DIR, true, "base directory where the experimental " + "datasets are");
        options.addOption(ARG_GRAPH_FILE_TO_LOAD, ARG_GRAPH_FILE_TO_LOAD, true,
                "graph file to load and " + "deserialize");
        options.addOption(ARG_NEW_FILE_FORMAT, ARG_NEW_FILE_FORMAT, true, "use new format?");
        options.addOption(ARG_TIMERS, ARG_TIMERS, true, "enable timers?");
        options.addOption(ARG_CONNECTED_QUERY_FILE, ARG_CONNECTED_QUERY_FILE, true, "A pool of connected queries");
        options.addOption(ARG_DISCONNECTED_QUERY_FILE, ARG_DISCONNECTED_QUERY_FILE, true,
                "A pool of disconnected queries");
        options.addOption(ARG_ADD_EDGES_FILE, ARG_ADD_EDGES_FILE, true,
                "file that contains edges to add " + "in batches");
        options.addOption(ARG_EDIT_EDGES_FILE, ARG_EDIT_EDGES_FILE, true,
                "file that contains edges to edit " + "in batches");
        options.addOption(ARG_DELETE_EDGES_FILE, ARG_DELETE_EDGES_FILE, true,
                "file that contains edges to delete in batches" + " (usually the original file but shuffled)");
        options.addOption(ARG_DELETION_PROBABILITY, ARG_DELETION_PROBABILITY, true,
                "fraction of edges" + "to delete in batches.");
        options.addOption(ARG_BATCH_SIZE, ARG_BATCH_SIZE, true, "size of each batch in the experiment.");
        options.addOption(ARG_NUM_BATCHES, ARG_NUM_BATCHES, true, "number of batches to execute in the experiment.");
        options.addOption(ARG_NUM_QUERIES, ARG_NUM_QUERIES, true, "number of queries to execute in the experiment.");
        options.addOption(ARG_UPDATE_OR_DELETEADD, ARG_UPDATE_OR_DELETEADD, true,
                "For weighted graphs, should we update or delete/add edges");

        options.addOption(ARG_CONNECTED_QUERY_PERCENTAGE, ARG_CONNECTED_QUERY_PERCENTAGE, true,
                "percentage of queries that are connected.");

        options.addOption(ARG_SEED_ADD_DELETE, ARG_SEED_ADD_DELETE, true, "The seed to " + "use for add/delete random");
        options.addOption(ARG_SEED_SRC_DST, ARG_SEED_SRC_DST, true,
                "The seed to use " + "for choosing two random src/destination");
        options.addOption(ARG_SEED_EDGE_WEIGHT, ARG_SEED_EDGE_WEIGHT, true,
                "The seed to use " + "for choosing random weights");
        options.addOption(ARG_SEED_EDGE_GEN, ARG_SEED_EDGE_GEN, true, "The seed to use " + "for choosing random edges");
        options.addOption(ARG_EXECUTOR_TYPE, ARG_EXECUTOR_TYPE, true,
                "type of the cont. sp executor: Options are: " + "baseline/uni-unw-arr/uni-unw-map/bi-unw.");
        options.addOption(ARG_EDGE_WEIGHT_RANGE, ARG_EDGE_WEIGHT_RANGE, true, "number of edge " + "weights");
        options.addOption(ARG_BACKTRACK, ARG_BACKTRACK, true, "should do backtracking or not");
        options.addOption(ARG_PRINT_DISTANCES, ARG_PRINT_DISTANCES, true, "should print distances or not");
        options.addOption(ARG_REPORT_LEVEL, ARG_REPORT_LEVEL, true, "reporting level (Error, Info, Debug)");
        options.addOption(ARG_LANDMARK_NUMBER, ARG_LANDMARK_NUMBER, true,
                "number of landmarks used by certain execution types");
        options.addOption(ARG_DROP_PROBABILITY, ARG_DROP_PROBABILITY, true,
                "The probability of droping diffs from vertices");
        options.addOption(ARG_DROP_TYPE, ARG_DROP_TYPE, true, "Drop type Random or Selective");
        options.addOption(ARG_BLOOM_TYPE, ARG_BLOOM_TYPE, true, "Bloom per 'Iteration' or 'Query'");
        options.addOption(ARG_DROP_MINIMUM, ARG_DROP_MINIMUM, true, "For selective, minimum vertex degree");
        options.addOption(ARG_DROP_MAXIMUM, ARG_DROP_MAXIMUM, true, "For selective, maximum vertex degree");
        return options;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Experiment Parameters:\n").append("baseDir:" + baseDir)
                .append(" graphFileToLoad: " + graphFileToLoad).append(" addEdgesFile: " + addEdgesFile)
                .append(" delEdgesFile:" + delEdgesFile).append(" editEdgesFile:" + editEdgesFile)
                .append(" deletionProbability: " + deletionProbability).append(" executorType: " + executorType)
                .append("\nnumQueries: " + numQueries)
                .append("\nConnectedQueryProbability: " + connectedQueryPercentage).append(" numBatches: " + numBatches)
                .append(" batchSize: " + batchSize).append(" addDeleteRand: " + addDeleteRand)
                .append(" srcDestRand: " + srcDestRand).append(" edgeWeightSeed: " + edgeWeightSeed)
                .append(" isWeighted: " + isWeighted).append(" edgeWeightRange: " + edgeWeightRange)
                .append(" backtrack: " + backtrack).append(" printDistances: " + printDistances);
        return sb.toString();
    }

    public void loadInitial() throws NumberFormatException, IOException {
        Graph graph = Graph.getInstance();

        var graphFile = new File(this.baseDir + this.graphFileToLoad);

        var hc = Files.asByteSource(graphFile).hash(Hashing.murmur3_32()).toString().toLowerCase();
        Graph.FILE_SUFFIX = (Graph.IS_WEIGHTED ? "weighted" : "unweighted") + "." + hc;
        String dataset_dir = this.baseDir + "/" + this.graphFileToLoad + "_" + Graph.FILE_SUFFIX;

        try {
            File dir = new File(dataset_dir);
            dir.mkdir();
            graph.deserializeAll(dataset_dir);
            System.out.println("graph deserialize! from: " + dataset_dir);
            TypeAndPropertyKeyStore.getInstance().deserializeAll(dataset_dir);
            System.out.println("Type KeyStore Deserialized");
            return;
        } catch (Exception e) {
            System.out.println("Serialized graph not found: " + e);
        }

        var in = new BufferedReader(new InputStreamReader(new FileInputStream(graphFile)));
        if (useNewFormat) {
            loadNew(in);
        } else {
            loadOld(in);
        }
        graph.finalizeChanges();

        try {
            System.out.println("Serializing this graph to save loading time in the future");
            graph.serializeAll(dataset_dir);
            System.out.println("Serializing the Type KeyStore");
            TypeAndPropertyKeyStore.getInstance().serializeAll(dataset_dir);
        } catch (Exception E) {
            System.out.println(E.toString());
        }
        in.close();
    }

    private void loadOld(BufferedReader in) throws IOException {
        Graph graph = Graph.getInstance();
        String line;
        int edgesCount = 0;
        int errorsCount = 0;
        long beginTime = System.nanoTime();

        while ((line = in.readLine()) != null) {
            if (line.isEmpty()) {
                continue;
            }

            String[] parts = line.split(SPLIT_REGEX);
            int expectedLength = (this.isWeighted) ? 3 : 2;
            /*It should be alright to handle a weighted graph as if it is unweighted*/
            if (parts.length < expectedLength) {
                errorsCount++;
                continue;
            }
            int fromVertex = Integer.parseInt(parts[0]);
            int toVertex = Integer.parseInt(parts[1]);
            double weight;
            short edgeType = edgeTypeId;
            if (isWeighted) {
                weight = Double.parseDouble(parts[2]);
                if (weight <= 0) {
                    System.out.println("potential error in this edge " + Arrays.toString(parts));
                }
                //Report.INSTANCE.debug("adding edge "+fromVertex+" --> "+toVertex+" w="+weight);
                if (parts.length == 4) {
                    edgeType = TypeAndPropertyKeyStore.getInstance().mapStringTypeToShortOrInsert(parts[3]);
                }

                graph.addEdgeTemporarily(fromVertex, toVertex, vertexTypeId, vertexTypeId, weight, edgeType);
            } else {
                //Report.INSTANCE.debug("adding edge "+fromVertex+" --> "+toVertex+" with no weight");
                if (parts.length == 3) {
                    edgeType = TypeAndPropertyKeyStore.getInstance().mapStringTypeToShortOrInsert(parts[2]);
                } else if (parts.length == 4) {
                    edgeType = TypeAndPropertyKeyStore.getInstance().mapStringTypeToShortOrInsert(parts[3]);
                }

                graph.addEdgeTemporarily(fromVertex, toVertex, vertexTypeId, vertexTypeId, edgeType);
            }
            edgesCount++;
            if (edgesCount % 1000000 == 0) {
                long t2 = System.nanoTime();
                System.out.println(edgesCount + " loaded in " + (t2 - beginTime) / 1000000.0);
                beginTime = t2;
            }
        }
        System.out.println(
                "Initial Edges prepared and added\t" + edgesCount + "\t" + errorsCount + " edges ignored due to " +
                        "incorrect format.");
    }

    private void loadNew(BufferedReader in) throws IOException {
        Graph graph = Graph.getInstance();
        String line;
        int edgesCount = 0;
        int errorsCount = 0;
        long beginTime = System.nanoTime();

        while ((line = in.readLine()) != null) {
            if (line.isEmpty()) {
                continue;
            }

            String[] parts = line.split(SPLIT_REGEX);
            int expectedLength = (this.isWeighted) ? 4 : 3;
            /*It should be alright to handle a weighted graph as if it is unweighted*/
            if (parts.length < expectedLength) {
                errorsCount++;
                continue;
            }
            int fromVertex = Integer.parseInt(parts[0]);
            int toVertex = Integer.parseInt(parts[1]);
            short edgeType = edgeTypeId;
            if (parts.length == 5) {
                edgeType = TypeAndPropertyKeyStore.getInstance().mapStringTypeToShortOrInsert(parts[4]);
            }
            if (isWeighted) {
                double weight = Double.parseDouble(parts[3]);
                if (weight <= 0) {
                    errorsCount++;
                    continue;
                }
                graph.addEdgeTemporarily(fromVertex, toVertex, vertexTypeId, vertexTypeId, weight, edgeType);
            } else {
                graph.addEdgeTemporarily(fromVertex, toVertex, vertexTypeId, vertexTypeId, edgeType);
            }
            edgesCount++;
            if (edgesCount % 1000000 == 0) {
                long t2 = System.nanoTime();
                System.out.println(edgesCount + " loaded in " + (t2 - beginTime) / 1000000.0);
                beginTime = t2;
            }
        }

        System.out.println(
                "Initial Edges prepared and added\t" + edgesCount + "\t" + errorsCount + " edges ignored due to " +
                        "incorrect format.");
    }

    public Graph getGraph() {
        Graph graph = Graph.getInstance();
        return graph;
    }

    /**
     * Prepare update data as a set of batches.
     * This function only generates insert or delete from given files
     * The delete probability decides if a batch should be an add or a delete batch.
     * Each batch should have exactly, `batchsize` queries. Half full batches are ignored.
     *
     * @throws IOException when an error occurs when reading an edge from the addEdges or
     *                     deleteEdges files.
     */
    private void prepareAddDeleteBatchesOld() throws IOException {

        System.out.println("ADD numBatches: " + numBatches);

        // I need to open both the files and then pass them around as necessary.
        InputStreamReader inAdd =
                new InputStreamReader(new FileInputStream(new File(this.baseDir + this.addEdgesFile)));
        BufferedReader bufferAddEdges = new BufferedReader(inAdd);

        InputStreamReader inDelete =
                new InputStreamReader(new FileInputStream(new File(this.baseDir + this.delEdgesFile)));
        BufferedReader bufferDeleteEdges = new BufferedReader(inDelete);

        double probability;
        System.out.println("numBatches: " + numBatches);
        for (int batchIdx = 0; batchIdx < numBatches; batchIdx++) {
            EdgeBatch nextBatch = new EdgeBatch(batchSize);
            for (int j = 0; j < batchSize; ++j) {
                probability = addDeleteRand.nextDouble();

                //System.out.println(probability);
                // this means alternating between add/delete
                if (this.deletionProbability == -1) {
                    if (batchIdx % 2 == 0) {
                        addEdgeToBatch(bufferAddEdges, nextBatch, BatchType.ADDITION);
                    } else {
                        addEdgeToBatch(bufferDeleteEdges, nextBatch, BatchType.DELETION);
                    }
                } else {
                    if (probability < this.deletionProbability) {
                        addEdgeToBatch(bufferDeleteEdges, nextBatch, BatchType.DELETION);
                    } else {
                        addEdgeToBatch(bufferAddEdges, nextBatch, BatchType.ADDITION);
                    }
                }
            }
            // If we couldn't put enough edges into the batch, we ignore it.
            if (nextBatch.currentEdgeIndex < batchSize) {
                System.out.println("Batch is empty BREAKING.");
                break;
            } else {
                batchData.add(nextBatch);
            }
        }
    }

    /**
     * This functions is used to prepare the initial diffs required by DC for each edge update!
     */
    void prepareBatchDiffsDC(){
        if (isDC(executorType)) {
            for (EdgeBatch edgeBatch : batchData) {
                Map<Integer, Map<Integer, DistancesDC.Diff2>> diffsData = new HashMap<>();
                for (int j = 0; j < edgeBatch.currentEdgeIndex; j++) {
                    var source = edgeBatch.fromVertexId[j];
                    var dest = edgeBatch.toVertexId[j];
                    var weight = isWeighted ? edgeBatch.weight[j] : 1;
                    var type = isRPQ(this.executorType) ? edgeBatch.edgeLabel[j] : edgeTypeId;
                    short diff;
                    if (edgeBatch.type[j] == BatchType.ADDITION) {
                        diff = 1;
                    } else {
                        diff = -1;
                    }
                    var entry = diffsData.computeIfAbsent(source, k -> new HashMap<>());
                    var diffs = entry.computeIfAbsent(dest, k -> new DistancesDC.Diff2((short) 0));
                    diffs.addDiff(weight, diff, type);
                }
                batchDiffs.add(diffsData);
            }
        }
    }

    /**
     * Prepare update data as a set of batches.
     * This function only generates insert or delete from given files
     * The delete probability decides if a batch should be an add or a delete batch.
     * Each batch should have exactly, `batchsize` queries. Half full batches are ignored.
     *
     * @throws IOException when an error occurs when reading an edge from the addEdges or
     *                     deleteEdges files.
     */
    private void prepareAddDeleteBatchesNew() throws IOException {
        for (int batchIdx = 1; batchIdx <= numBatches; batchIdx++) {
            System.out.printf("Preparing batch %d of %d\n", batchIdx, numBatches);
            InputStreamReader inputStreamReader =
                    new InputStreamReader(new FileInputStream(this.baseDir + "batch-" + batchIdx + ".txt"));
            BufferedReader bufferedReaderEdges = new BufferedReader(inputStreamReader);
            int errorsCount = 0;
            int additionsCount = 0;
            int deletionsCount = 0;
            int loadedWeights = 0;
            int loadedTypes = 0;
            List<String[]> lines = new ArrayList<>();
            String line;
            while (((line = bufferedReaderEdges.readLine()) != null)) {
                if (line.isEmpty()) {
                    continue;
                }
                String[] parts = line.split(SPLIT_REGEX);
                if (parts.length < 3) {
                    errorsCount++;
                    continue;
                }
                lines.add(parts);
            }
            EdgeBatch nextBatch = new EdgeBatch(lines.size());
            for (String[] parts : lines) {
                BatchType type;
                if (parts[2].equals("+1")) {
                    type = BatchType.ADDITION;
                    additionsCount++;
                } else {
                    type = BatchType.DELETION;
                    deletionsCount++;
                }
                int fromVertex = Integer.parseInt(parts[0]);
                int toVertex = Integer.parseInt(parts[1]);
                short edgeLabel = -1;
                double newEdgeWeight;
                if (parts.length == 5) {
                    edgeLabel = TypeAndPropertyKeyStore.getInstance().mapStringTypeToShortOrInsert(parts[4]);
                    loadedTypes++;
                }
                if (parts.length >= 4) {
                    newEdgeWeight = Double.parseDouble(parts[3]);
                    if (newEdgeWeight <= 0) {
                        errorsCount++;
                        continue;
                    }
                    loadedWeights++;
                } else {
                    newEdgeWeight = edgeWeightGenerator.generateEdgeWeight();
                }
                nextBatch.insertEdge(fromVertex, toVertex, newEdgeWeight, edgeLabel, type);
            }
            // If we couldn't put enough edges into the batch, we ignore it.
            if (nextBatch.currentEdgeIndex < 0) {
                System.out.println("Batch is empty, ignoring.");
            } else {
                System.out.printf("Loaded batch with %d changes (%d additions and %d deletions)%s%s.\n",
                        nextBatch.currentEdgeIndex, additionsCount, deletionsCount,
                        loadedWeights > 0 ? ". " + loadedWeights + " had weights" : "",
                        loadedTypes > 0 ? ". " + loadedTypes + " had types" : "");
                if (errorsCount > 0) {
                    System.out.printf("***** Ignored %d edges due to format errors.\n", errorsCount);
                }
                batchData.add(nextBatch);
            }
        }
    }

    private void addEdgeToBatch(BufferedReader bufferedReader, EdgeBatch batch, BatchType type) throws IOException {
        String line;

        while (((line = bufferedReader.readLine()) != null)) {
            if (line.isEmpty()) {
                System.out.println("When preparing a batch found an empty line in either add or" +
                        " delete edges file. - empty line");

                continue;
            }
            String[] parts = line.split(SPLIT_REGEX);
            short edgeLabel = -1;
            double newEdgeWeight = edgeWeightGenerator.generateEdgeWeight();
            if (parts.length == 4) {
                newEdgeWeight = Double.parseDouble(parts[2]);
                edgeLabel = TypeAndPropertyKeyStore.getInstance().mapStringTypeToShortOrInsert(parts[3]);
            } else if (parts.length == 3) {
                newEdgeWeight = Double.parseDouble(parts[2]);
            } else if (parts.length != 2) {
                Report.INSTANCE.error("When preparing a batch found an error line in either add or" +
                        " delete edges file. - length is not two! --> " + line + " = " + parts.length);
                continue;
            }
            batch.insertEdge(Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()), newEdgeWeight,
                    edgeLabel, type);
            return;
        }
    }

    // dummy function that only help profiling
    public void finsihBatch() {
        return;
    }

    public void startProfiling() {
        return;
    }

    /**
     * This is the function that run the experiment
     * 1- Apply each batch
     * 2- Run the queries following the execution plan
     */
    private void applyBatchesAndMeasure() {
        int addedEdgeCount = 0;
        int deletedEdgeCount = 0;
        int numBatchesProcessed = 0;
        int batch_counter = 0;
        long totalGCtime = 0;
        long accurateTotalTimeMicro = 0;
        long accurateUpdateTimeMicro = 0;
        long accurateFinalizeTimeMicro = 0;

        Report.INSTANCE.error("Number of batches: " + batchData.size());
        System.out.println("time used for GC BEFORE all batches (ms) = " +
                ManagementFactory.getGarbageCollectorMXBeans().stream().mapToLong(mxBean -> mxBean.getCollectionTime())
                        .sum() + " ms");
        // loop on update batches
        var timer = new Timer();
        for (EdgeBatch edgeBatch : batchData) {
            // loop in the batch
            if (isDC(executorType)) {
                Graph.INSTANCE.edgeDiffs = batchDiffs.get(batch_counter);
            }
            batch_counter++;
            timer.reset();
            for (int j = 0; j < edgeBatch.currentEdgeIndex; j++) {
                var source = edgeBatch.fromVertexId[j];
                var dest = edgeBatch.toVertexId[j];
                var weight = isWeighted ? edgeBatch.weight[j] : 1;
                var type = isRPQ(this.executorType) ? edgeBatch.edgeLabel[j] : edgeTypeId;
                if (edgeBatch.type[j] == BatchType.ADDITION) {
                    if (isWeighted) {
                        Graph.INSTANCE.addEdgeTemporarily(source, dest, vertexTypeId, vertexTypeId, weight, type);
                    } else {
                        Graph.INSTANCE.addEdgeTemporarily(source, dest, (short) -1, (short) -1, type);
                    }
                    addedEdgeCount++;
                } else {
                    Graph.INSTANCE
                            .deleteEdgeTemporarily(edgeBatch.fromVertexId[j], edgeBatch.toVertexId[j], (short) -1);
                    deletedEdgeCount++;
                }
            }
            long updateIntervalMicro = timer.elapsedMicros();
            accurateUpdateTimeMicro += updateIntervalMicro;

            // After applying all changes to the graph (with in a batch), finalize changes and run algorithms!
            /**
             * Finalize changes for Baseline executors:
             * 1- UNW_BASELINE
             * 2- W_BASELINE
             * 3- DIJKSTRA
             *
             */

            // Convert temp changes from above to permanent before we run base line approaches
            // For Continuous plans, we need to keep these changes temporary.
            timer.reset();
            if (isBaseLine(executorType)) {
                Graph.INSTANCE.finalizeChanges();
            }

            long finalizeIntervalMicro = timer.elapsedMicros();
            accurateFinalizeTimeMicro += finalizeIntervalMicro;

            // Run queries!

            /* Run GC here so we reduce the probability of GC during batch execution */

            //Runtime r = Runtime.getRuntime();
            //r.gc();
            //System.out.println("Free-memory-before-Batch "+batch_counter+ " "+r.freeMemory());
            // Execute batches
            long gcStartTime = 0;
            long gcTime = 0;
            if (printDistances) {
                gcStartTime = ManagementFactory.getGarbageCollectorMXBeans().stream()
                        .mapToLong(mxBean -> mxBean.getCollectionTime()).sum();
            }
            timer.reset();
            ContinuousShortestPathsExecutor.getInstance().execute();
            long batchIntervalMicro = timer.elapsedMicros();
            accurateTotalTimeMicro += batchIntervalMicro;
            if (printDistances) {
                long gcEndTime = ManagementFactory.getGarbageCollectorMXBeans().stream()
                        .mapToLong(mxBean -> mxBean.getCollectionTime()).sum();
                gcTime = gcEndTime - gcStartTime;
                totalGCtime += gcTime;
                System.gc();
                System.gc();
            }
            finsihBatch();
            //System.out.println("Free-memory-after-Batch "+batch_counter+ " "+r.freeMemory());

            if (Report.INSTANCE.appReportingLevel == Report.Level.INFO) {
                batchTimes.add(batchIntervalMicro);
            }

            finsihBatch();

            /**
             *
             * Now, make changes permanent if the plan is not baseline
             *
             */
            timer.reset();
            if (!isBaseLine(executorType)) {
                Graph.INSTANCE.finalizeChanges();
            }

            finalizeIntervalMicro = timer.elapsedMicros();
            accurateFinalizeTimeMicro += finalizeIntervalMicro;

            numBatchesProcessed++;
            if (numBatchesProcessed % 100 == 0) {
                System.out.println(String.format("finished %s batches", numBatchesProcessed));
            }

            Report.INSTANCE
                    .error("BatchTime " + batch_counter + " " + Timer.elapsedMicroToMilliString(batchIntervalMicro));
            Report.INSTANCE
                    .error("UpdateTime " + batch_counter + " " + Timer.elapsedMicroToMilliString(updateIntervalMicro));
            Report.INSTANCE
                    .error("FinalizeTime " + batch_counter + " " + Timer.elapsedMicroToMilliString(finalizeIntervalMicro));

            // UNCOMMENT THE CODE BELOW TO WRITE DOWN DISTANCES
            //System.out.println("\n\nBatch # "+batch_counter);
            if (printDistances) {
                long memory = getMBmemory();

                Report.INSTANCE.error("BatchMemory " + batch_counter + " " + memory + " MB");
                Report.INSTANCE.error("BatchTimeGC " + batch_counter + " " + gcTime + " ms");

                List<Long> distances = new LinkedList<>();
                for (ContinuousShortestPathPlan shortestPathPlan : ContinuousShortestPathsExecutor
                        .getShortestPathPlans()) {
                    distances.add(shortestPathPlan.getSrcDstDistance());
                }

                Report.INSTANCE.error("BatchAnswer " + batch_counter + " " + Arrays.toString(distances.toArray()));
                srcDstDistances.add(distances);

                if (isDiff(executorType)) {
                    Report.INSTANCE
                            .error("BatchDistance " + batch_counter + " " + Arrays.toString(getSizesOfDistances()));
                    Report.INSTANCE.error("RecalculateNumber " + batch_counter + " " +
                            Arrays.toString(getRecalculateNumbers()));
                    Report.INSTANCE.error("Keep = " + DistancesWithDropBloom.keepCuonter + " Drpo = " +
                            DistancesWithDropBloom.dropoCuonter);
                    Report.INSTANCE.error("RecalculateStats " + batch_counter + " " +
                            getRecalculateStats());
                }
            }

            if (Report.INSTANCE.appReportingLevel == Report.Level.INFO) {
                if (!isBaseLine(executorType)) {
                    System.out.println("===================== BATCH Summary ========================");

                    //System.out.println("distances size = " + Arrays.toString(getSizesOfDistances()));
                    printPath();
                    //System.out.println("Minimum distances size = " + Arrays.toString(getMinimumSizeOfDistances()));
                }
            }
            // TODO, for testing only
            //printPath();
        }

        System.out.printf("time to process all batches: %s\n", Timer.elapsedMicroToMilliString(accurateTotalTimeMicro));
        System.out.printf("time to update all batches: %s\n", Timer.elapsedMicroToMilliString(accurateUpdateTimeMicro));
        System.out.printf("time to finalize all batches: %s\n", Timer.elapsedMicroToMilliString(accurateFinalizeTimeMicro));
        System.out.printf("total time for all batches: %s\n",
                Timer.elapsedMicroToMilliString(accurateTotalTimeMicro + accurateUpdateTimeMicro));
        if (printDistances) {
            System.out.println("time used for GC in all batches (ms) = " + totalGCtime + " ms");
        }
        System.out.printf("Edges processed (added + deleted): %d (%d + %d)\n", (addedEdgeCount + deletedEdgeCount),
                addedEdgeCount, deletedEdgeCount);
    }

    private boolean isDiff(ExecutorType executorType) {
        if (!isBaseLine(executorType)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Function to pick shortest path queries from the file
     *
     * @throws IOException
     */
    private void prepareAndRegisterContinuousShortestPathQueries() throws IOException {

        String[] continuousSPQueries = new String[numQueries + 1];
        List<Pair<Integer, Integer>> srcDstPairs =
                getQueries(); // --> This function already retrieve the amount of requested queries only!
        StructuredQuery[] structuredQueries = new StructuredQuery[numQueries + 1];

        // This solves the problem of having less than expected queries in the files
        if (numQueries >= srcDstPairs.size()) {
            System.out.println("#requested_queries=" + numQueries);
            numQueries = srcDstPairs.size() - 1;
            System.out.println("#available_queries=" + numQueries);
        }

        /**
         * This is a hack and probably not the best way to remember the number of queries at diff level.
         */
        Distances.numQueries = numQueries;
        DistancesDC.numQueries = numQueries;

        // it starts from 0, because we already initialized the first plan
        for (int i = 1; i <= numQueries; i++) {
            Pair<Integer, Integer> query = srcDstPairs.get(i);
            int source = query.a;
            int destination = query.b;
            if (isWeighted) {
                continuousSPQueries[i] = "CONTINUOUSLY SHORTEST PATH (" + source + ", " + destination +
                        ") WEIGHTS ON WEIGHT FILE '/dev/null'";
            } else {
                continuousSPQueries[i] =
                        "CONTINUOUSLY SHORTEST PATH (" + source + ", " + destination + ") FILE '/dev/null'";
            }

            structuredQueries[i] = new StructuredQueryParser().parse(continuousSPQueries[i]);

            System.out.println(String.format("*** query %s: %s", i, continuousSPQueries[i]));
            ContinuousShortestPathsExecutor.getInstance().addShortestPathPlan(
                    (ContinuousShortestPathPlan) new ContinuousShortestPathPlanner(structuredQueries[i])
                            .plan(i, executorType, backtrack, this.dropProbability, this.dropType, this.bloomType,
                                    this.dropMinimum, this.dropMaximum));
        }
    }

    /**
     * Function to pick some queries and collect some statistics about them
     *
     * @throws IOException
     */
    private void collectQueryStats(int numQueries) throws IOException {

        String[] continuousSPQueries = new String[numQueries];
        List<Pair<Integer, Integer>> srcDstPairs =
                getQueries(); // --> This function already retrieve the amount of requested queries only!
        StructuredQuery[] structuredQueries = new StructuredQuery[numQueries];

        // This solves the problem of having less than expected queries in the files
        if (numQueries > srcDstPairs.size()) {
            System.err.println("Number of samples queries is more than available ones in file");
            numQueries = srcDstPairs.size();
            System.out.println("#requested_queries=" + numQueries);
            System.out.println("#available_queries=" + srcDstPairs.size());
        }

        Distances.numQueries = numQueries;

        for (int i = 1; i < numQueries; i++) {
            Pair<Integer, Integer> query = srcDstPairs.get(i);
            int source = query.a;
            int destination = query.b;
            if (isWeighted) {
                continuousSPQueries[i] =
                        "CONTINUOUSLY SHORTEST PATH (" + source + ", " + destination + ") WEIGHTS ON WEIGHT FILE 'gym'";
            } else {
                continuousSPQueries[i] = "CONTINUOUSLY SHORTEST PATH (" + source + ", " + destination + ") FILE 'gym'";
            }

            structuredQueries[i] = new StructuredQueryParser().parse(continuousSPQueries[i]);

            /**
             * Before registering these queries, and initialize them, I would like to pick the first 100 (if available) queries and
             * use them to detect the average number of distances for these queries. I will use this number to set the expected number of
             * elements in Bloom Filter.
             *
             */

            ContinuousShortestPathsExecutor.getInstance().addShortestPathPlan(
                    (ContinuousShortestPathPlan) new ContinuousShortestPathPlanner(structuredQueries[i])
                            .plan(i, ExecutorType.SPSP_W_CDD, backtrack, this.dropProbability, this.dropType,
                                    this.bloomType, this.dropMinimum, this.dropMaximum));

            System.out.println(String.format("*** collecting stats from query %s: %s", i, continuousSPQueries[i]));
        }

        System.out.println("Running queries for stats");
        ContinuousShortestPathsExecutor.getInstance().execute();

        // get distance
        int[] distances = getSizesOfDistances();
        double averageDistance = 0;
        Report.INSTANCE.error("Sampled BatchDistance " + " " + Arrays.toString(distances));

        // get answer
        List<Long> answers = new LinkedList<>();
        for (ContinuousShortestPathPlan shortestPathPlan : ContinuousShortestPathsExecutor.getShortestPathPlans()) {
            answers.add(shortestPathPlan.getSrcDstDistance());
        }
        Report.INSTANCE.error("Sampled Answer " + " " + Arrays.toString(answers.toArray()));

        for (int d = 0; d < distances.length; d++) {
            averageDistance += distances[d];
        }

        averageDistance = averageDistance / distances.length;
        int bloomFilterSize = (int) Math.ceil(averageDistance * 2);

        MultiQueryDiffBloomPerQuery.expectedElementPerFilter = bloomFilterSize;
        System.out.println("Bloom filter expected number of elements is " + bloomFilterSize);
        Report.INSTANCE.error("Bloom filter expected number of elements is " + bloomFilterSize);

        ContinuousShortestPathsExecutor.getInstance().initializeStats();
        ContinuousShortestPathsExecutor.getInstance().reset();
        Distances.numQueries = this.numQueries;
        Distances.realDiffInitialized = false;
    }

    /**
     * Prepare a list of queries using a predefined two sets of connected and disconnected queries
     * The selection of a connected or disconnected query depends on a random variable
     * based on the seed value: srcDestRand
     *
     * @return A list of queries, each represented as pair<int>
     * @throws IOException
     */
    private List<Pair<Integer, Integer>> getQueries() throws IOException {

        // Prepare connected queries, add them to the buffer
        List<Pair<Integer, Integer>> connectedQueries = new ArrayList<>();
        System.out.println("connectedQueryFile: " + connectedQueryFile);
        BufferedReader brConnected = new BufferedReader(
                new InputStreamReader(new FileInputStream(new File(this.baseDir + this.connectedQueryFile))));
        String line;
        String[] split;
        int counter = 1;
        while ((line = brConnected.readLine()) != null && counter <= numQueries) {
            split = line.split(SPLIT_REGEX);
            connectedQueries.add(new Pair<Integer, Integer>(Integer.parseInt(split[0]), Integer.parseInt(split[1])));
            counter++;
        }
        brConnected.close();

        // Prepare disconnected queries, add them to the buffer
        List<Pair<Integer, Integer>> disconnectedQueries = new ArrayList<>();
        if (connectedQueryPercentage < 1) {
            System.out.println("DisConnectedQueryFile: " + disconnectedQueryFile);
            BufferedReader brDisconnected = new BufferedReader(
                    new InputStreamReader(new FileInputStream(new File(this.baseDir + this.disconnectedQueryFile))));
            while ((line = brDisconnected.readLine()) != null && counter <= numQueries) {
                split = line.split(SPLIT_REGEX);
                disconnectedQueries
                        .add(new Pair<Integer, Integer>(Integer.parseInt(split[0]), Integer.parseInt(split[1])));
            }
            brDisconnected.close();
        }

        // List of used queries
        List<Pair<Integer, Integer>> queries = new ArrayList<>();
        int nextConnectedQuery = 0;
        int nextDisconnectedQuery = 0;
        // add dummy initial query
        queries.add(new Pair<Integer, Integer>(0, 0));
        for (int i = 1; i <= numQueries; ++i) {
            // check if there is any available connected queries!
            if (srcDestRand.nextDouble() < connectedQueryPercentage && nextConnectedQuery < connectedQueries.size()) {
                queries.add(connectedQueries.get(nextConnectedQuery++));

                // check for disconnected queries!
            } else if (nextDisconnectedQuery < disconnectedQueries.size()) {
                queries.add(disconnectedQueries.get(nextDisconnectedQuery++));
            }
        }
        return queries;
    }

    private double getAverage() {
        if (Report.INSTANCE.appReportingLevel == Report.Level.INFO) {
            System.out.println("BatchTimes.size: " + batchTimes.size());
            long sum = batchTimes.stream().mapToLong(Long::longValue).sum();
            return sum * 1.0 / batchTimes.size();
        } else {
            return 0;
        }
    }

    /**
     * This is the function to prepare and then call for an experiment execution
     *
     * @throws NumberFormatException
     * @throws IOException
     */
    private void prepareAndExecuteExperiment(int bloomFilterSize) throws NumberFormatException, IOException {
        System.out.println("*** Graphflow: Continuous Shortest Path Experiment ***");
        System.out.println("*** Graphflow: ExecutorType: " + executorType.toString() + " ***");

        // 1 - Load data
        var timer = new Timer();
        loadInitial();
        System.out.println("Memory after loading graph " + getMBmemory());
        System.out.println("Initial graph load time: " + timer.elapsedDurationString());

        System.out.println("Graph statistics: ");
        System.out.println("#Vertices = " + Graph.getInstance().getVertexCount());
        System.out.println("#Min, Max, Average Degree, Count MoreThanMax Forward = " +
                Arrays.toString(Graph.getInstance().getDegreeStats(Graph.Direction.FORWARD, 10)));
        System.out.println("#Max, Min, Average Degree, Count MoreThanMax Backward = " +
                Arrays.toString(Graph.getInstance().getDegreeStats(Graph.Direction.BACKWARD, 100)));

        Graph.getInstance().printDistribution(Graph.Direction.FORWARD);
        Graph.getInstance().printDistribution(Graph.Direction.BACKWARD);
        // 2 - prepare update queries, by randomly selecting from given files using given seeds!

        System.out.println("\n\n\n ====== \n" + updateAction);

        timer.reset();
        if (useNewFormat) {
            prepareAddDeleteBatchesNew();
        } else {
            prepareAddDeleteBatchesOld();
        }
        // One extra step required for DC
        if(isDC(executorType))
            prepareBatchDiffsDC();

        System.out.println("Batches prep time: " + timer.elapsedDurationString());
        System.out.println("Memory after batches " + getMBmemory());

        System.gc();
        System.gc();
        finsihBatch();
        startProfiling();

        /**
         * Before registering these queries, and initialize them, I would like to pick the first 100 (if available) queries and
         * use them to detect the average number of distances for these queries. I will use this number to set the expected number of
         * elements in Bloom Filter.
         */
        if (ExecutorType.isProb(executorType)) {
            MultiQueryDiffBloomPerQuery.expectedElementPerFilter = bloomFilterSize;
        }
        //collectQueryStats(10);

        // 3 - prepare query list, and register them for execution

        long gcStartTime =
                ManagementFactory.getGarbageCollectorMXBeans().stream().mapToLong(mxBean -> mxBean.getCollectionTime())
                        .sum();
        timer.reset();
        prepareAndRegisterContinuousShortestPathQueries();
        System.out.println("Finished initializing! ");
        System.out.printf("Initial differential time: %s\n", timer.elapsedDurationString());
        System.out.println("Memory after registering queries " + getMBmemory());
        long gcEndTime =
                ManagementFactory.getGarbageCollectorMXBeans().stream().mapToLong(mxBean -> mxBean.getCollectionTime())
                        .sum();
        System.out.printf("Initial GC time: %s\n", Timer.elapsedDurationString(gcEndTime - gcStartTime));

        // 3.5 - warm up
        System.out.println("Start warming up phase for "+DEFAULT_ARG_PREPARATION_QUERIES+" times!");
        // warming up - X times before applying batches?
        timer.reset();
        for (int ii = 0; ii < DEFAULT_ARG_PREPARATION_QUERIES; ii++) {
            ContinuousShortestPathsExecutor.getInstance().execute();
        }
        System.out.println("Warming up time: "+ timer.elapsedDurationString());
        System.out.println("Memory after warmup " + getMBmemory());

        List<Long> distances = new LinkedList<>();
        for (ContinuousShortestPathPlan shortestPathPlan : ContinuousShortestPathsExecutor.getShortestPathPlans()) {
            distances.add(shortestPathPlan.getSrcDstDistance());
            //System.out.println(shortestPathPlan.getSource()+"-"+shortestPathPlan.getDestination()+
            //        " --> "+shortestPathPlan.getSrcDstDistance());
        }

        Report.INSTANCE.error("BatchMemory 0 " + getMBmemory() + " MB");
        Report.INSTANCE.error("BatchAnswer 0 " + Arrays.toString(distances.toArray()));
        if (isDiff(executorType)) {
            Report.INSTANCE.error("BatchDistance 0 " + Arrays.toString(getSizesOfDistances()));
            Report.INSTANCE.error("RecalculateNumber 0 " + Arrays.toString(getRecalculateNumbers()));
            Report.INSTANCE.error("Keep = " + DistancesWithDropBloom.keepCuonter + " Drpo = " +
                    DistancesWithDropBloom.dropoCuonter);
            Report.INSTANCE.error("RecalculateStats 0 " + getRecalculateStats());
        }

        srcDstDistances.add(distances);

        // TODO, for testing only
        //printPath();

        System.out.println("warming up phase is done - start real batches");

        for (int ii = 0; ii < DEFAULT_ARG_PREPARATION_QUERIES; ii++) {
            ContinuousShortestPathsExecutor.getInstance().initializeStats();
        }

        // 4 - Run the experiment!
        // run garbage collector before batch to reduce the probability of running it during batch update
        // This made the system slower in most queries
        //System.gc();

        gcStartTime =
                ManagementFactory.getGarbageCollectorMXBeans().stream().mapToLong(mxBean -> mxBean.getCollectionTime())
                        .sum();
        timer.reset();
        applyBatchesAndMeasure();
        long executionTime = timer.elapsedMillis();
        gcEndTime =
                ManagementFactory.getGarbageCollectorMXBeans().stream().mapToLong(mxBean -> mxBean.getCollectionTime())
                        .sum();
        Report.INSTANCE.error("BatchMemory N " + getMBmemory() + " MB");
        Report.INSTANCE.error("BatchAnswer N " + Arrays.toString(distances.toArray()));
        if (isDiff(executorType)) {
            Report.INSTANCE.error("VerticesWithDiff N " + Arrays.toString(getNumberOfVertices()));
            Report.INSTANCE.error("BatchDistance N " + Arrays.toString(getSizesOfDistances()));
            Report.INSTANCE.error("RecalculateNumber N " + Arrays.toString(getRecalculateNumbers()));
            Report.INSTANCE.error("Keep = " + DistancesWithDropBloom.keepCuonter + " Drpo = " +
                    DistancesWithDropBloom.dropoCuonter);
            Report.INSTANCE.error("RecalculateStats N " + getRecalculateStats());
        }
        System.out.printf("Batches GC time: %s\n", Timer.elapsedDurationString(gcEndTime - gcStartTime));



// We currently print them after each batch

        System.out.println("Printing stats for Differential Executors in DEBUG");
        if (isDiff(executorType) && Report.INSTANCE.appReportingLevel == Report.Level.DEBUG)
            //print diffs for all queries
            ContinuousShortestPathsExecutor.getInstance().printStats(); //.printAllDiffs();
        System.out.println("=========================================");

        if (Report.INSTANCE.appReportingLevel == Report.Level.INFO) {
            System.out.println("Query Times:\n");
            long[][] queryTimes = getQueryTimes();
            // Indices start from 0 because "query 0"  is not added as a plan.
            for (int q = 0; q < numQueries; q++) {
                //Remove warm-up runs
                long[] thisQueryTime = new long[numBatches];

                for (int b = 0; b < numBatches; b++) {
                    thisQueryTime[b] = queryTimes[q][b + DEFAULT_ARG_PREPARATION_QUERIES];
                }

                System.out.println("Query#" + (q + 1) + ":" + Arrays.toString(thisQueryTime));
                //System.out.println("Query#"+(q+1)+":"+Arrays.toString(queryTimes[q])+"\n");
            }
        }

/*
        if (isDiff(executorType))
            System.out.println("distances size = " + Arrays.toString(getSizesOfDistances()));
*/

        // 5 - Reporting
        System.out.println("*** Experiment Finished ***");
        System.out.println("*** Executor Type: " + executorType.name() + " ***");
        System.out.printf("Execution time (ApplyBatch+UpdateAnswer): %s\n", Timer.elapsedDurationString(executionTime));
        long gcTime =
                ManagementFactory.getGarbageCollectorMXBeans().stream().mapToLong(mxBean -> mxBean.getCollectionTime())
                        .sum();
        System.out.println("Total GC time = " + Timer.elapsedDurationString(gcTime));
        //System.out.println("Diff used="+ DiffPool.createPool(0).getUsedSize()+ " - capacity="+DiffPool.createPool(0).getCapacity());
        //System.out.println("IterationDistancePair used="+ IterationDistancePairPool.createPool(0).getUsedSize()+ " - capacity="+IterationDistancePairPool.createPool(0).getCapacity());



        /*
        if (ExecutorType.UNW_BASELINE == executorType) {
            System.out.println("ShortestPathExecutor.totalTimeTaken: " +
                ShortestPathExecutor.totalTimeTaken / 1000000.0 + " ms.");
        }
        */

        if (Report.INSTANCE.appReportingLevel == Report.Level.INFO) {
            System.out.println("total batch times (ContinuouQueryTime)= " +
                    batchTimes.stream().mapToLong(Long::longValue).sum() / 1000000.0 + " ms");
            System.out.println("First batch time = " + batchTimes.get(0) / 1000000.0 + " ms.");
            System.out.println("Average batch time = " + getAverage() / 1000000.0 + " ms.");
            System.out.println("Last batch time = " + batchTimes.get(batchTimes.size() - 1) / 1000000.0 + " ms.");

            System.out.println("Batch times: " + Arrays.toString(batchTimes.toArray()));
        }
        if (srcDstDistances.size() < 2) {
            distances = new LinkedList<>();
            for (ContinuousShortestPathPlan shortestPathPlan : ContinuousShortestPathsExecutor.getShortestPathPlans()) {
                distances.add(shortestPathPlan.getSrcDstDistance());
            }
            srcDstDistances.add(distances);
        }
        System.out.println("Printing distances:");
        for (int i = 0; i < srcDstDistances.size(); ++i) {
            System.out.println("index " + i + ": " + Arrays.toString(srcDstDistances.get(i).toArray()));
        }
    }

    public long[][] getQueryTimes() {
        List<ContinuousShortestPathPlan> differentialPlans = ContinuousShortestPathsExecutor.getShortestPathPlans();
        long[][] queryTime = new long[differentialPlans.size()][];
        for (int i = 0; i < differentialPlans.size(); i++) {
            queryTime[i] = differentialPlans.get(i).getQueryTimes();
        }

        return queryTime;
    }

    public int[] getRecalculateNumbers() {
        List<ContinuousShortestPathPlan> differentialPlans = ContinuousShortestPathsExecutor.getShortestPathPlans();
        int[] sizesArray = new int[differentialPlans.size()];
        for (int i = 0; i < differentialPlans.size(); i++) {
            sizesArray[i] = ((ContinuousDiffBFSShortestPathPlan) differentialPlans.get(i)).getRecalculateNumbers();
        }
        return sizesArray;
    }

    public Map getRecalculateStats() {
        List<ContinuousShortestPathPlan> differentialPlans = ContinuousShortestPathsExecutor.getShortestPathPlans();
        Map<Integer,Integer> vertexRecalculateStats = new HashMap<Integer,Integer> (1);
        Map<Integer,Integer> degreeCountStats = new HashMap<Integer,Integer> (1);
        Map<Integer,Integer> degreeRecalculateStats = new HashMap<Integer,Integer> (1);
        Map<Integer,Float> degreeAvgRecalculate = new HashMap<Integer,Float> (1);
        int degree = 0;
        for (int i = 0; i < differentialPlans.size(); i++) {
            vertexRecalculateStats = ((ContinuousDiffBFSShortestPathPlan) differentialPlans.get(i)).getRecalculateStat();
            for (var e : vertexRecalculateStats.entrySet()) {
                degree = Graph.INSTANCE.getVertexDegree(e.getKey(), Graph.Direction.FORWARD);
                degreeCountStats.put(degree, degreeCountStats.getOrDefault(degree, 0) + 1);
                degreeRecalculateStats.put(degree, degreeRecalculateStats.getOrDefault(degree, 0) + e.getValue());
            }
        }

        for (var d:degreeCountStats.keySet()){
            degreeAvgRecalculate.put(d,(float) degreeRecalculateStats.get(d)/degreeCountStats.get(d));
        }

        return degreeAvgRecalculate;
    }

    public int[] getSizesOfDistances() {
        List<ContinuousShortestPathPlan> differentialPlans = ContinuousShortestPathsExecutor.getShortestPathPlans();
        int[] sizesArray = new int[differentialPlans.size()];
        for (int i = 0; i < differentialPlans.size(); i++) {
            sizesArray[i] = ((ContinuousDiffBFSShortestPathPlan) differentialPlans.get(i)).getSizeOfDistances();
        }
        return sizesArray;
    }

    public int [] getNumberOfVertices(){
        List<ContinuousShortestPathPlan> differentialPlans = ContinuousShortestPathsExecutor.getShortestPathPlans();
        int[] sizesArray = new int[differentialPlans.size()];
        for (int i = 0; i < differentialPlans.size(); i++) {
            sizesArray[i] = ((ContinuousDiffBFSShortestPathPlan) differentialPlans.get(i)).getNumberOfVertices();
        }
        return sizesArray;
    }

    public void printPath() {
        List<ContinuousShortestPathPlan> differentialPlans = ContinuousShortestPathsExecutor.getShortestPathPlans();

        for (int i = 0; i < differentialPlans.size(); i++) {
            System.out.println("***************************************");

            System.out.println(
                    differentialPlans.get(i).getSource() + " ==> " + differentialPlans.get(i).getDestination() + " = " +
                            differentialPlans.get(i).getSrcDstDistance());
            differentialPlans.get(i).printStats();
        }

        System.out.println("***************************************");
    }

    public int[] getMinimumSizeOfDistances() {
        List<ContinuousShortestPathPlan> differentialPlans = ContinuousShortestPathsExecutor.getShortestPathPlans();
        int[] sizesArray = new int[differentialPlans.size()];
        for (int i = 0; i < differentialPlans.size(); i++) {
            sizesArray[i] = ((ContinuousDiffBFSShortestPathPlan) differentialPlans.get(i)).getMinimumSizeOfDistances();
        }
        return sizesArray;
    }

    public static class EdgeBatch {
        public int[] fromVertexId;
        public int[] toVertexId;
        public double[] weight;
        public short[] edgeLabel;
        public BatchType[] type;
        public int currentEdgeIndex = 0;
        int size = 0;

        public EdgeBatch(int batchSize) {
            this.size = batchSize;
            init();
        }

        void init() {
            this.toVertexId = new int[this.size];
            this.fromVertexId = new int[this.size];
            this.weight = new double[this.size];
            this.edgeLabel = new short[this.size];
            this.type = new BatchType[this.size];
        }

        void insertEdge(int fromVertex, int toVertex, BatchType type) {

            //System.out.println(this.currentEdgeIndex);

            this.fromVertexId[currentEdgeIndex] = fromVertex;
            this.toVertexId[currentEdgeIndex] = toVertex;
            this.type[currentEdgeIndex] = type;
            this.currentEdgeIndex++;
        }

        public void insertEdge(int fromVertex, int toVertex, double weight, short label, BatchType type) {
            this.edgeLabel[currentEdgeIndex] = label;
            this.weight[currentEdgeIndex] = weight;
            insertEdge(fromVertex, toVertex, type);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("EdgeBatch: size=");
            sb.append(size);
            sb.append(", edge=[");
            for (int i = 0; i < currentEdgeIndex; i++) {
                sb.append("[");
                sb.append(fromVertexId[i]);
                sb.append(",");
                sb.append(toVertexId[i]);
                sb.append(",");
                sb.append(weight[i]);
                sb.append(",");
                sb.append(type[i].toString());
                sb.append("]");
                if (i < currentEdgeIndex - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]");
            return sb.toString();
        }
    }
}
