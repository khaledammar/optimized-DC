package ca.waterloo.dsg.graphflow;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.*;
import java.util.Random;

/**
 * Created by U6035886 on 2018-08-14.
 */
public class GenerateWeightedGraphs {

    public static String ARG_SEED_EDGE_WEIGHT = "edgeWeightSeed";

    //
    //This is the base directory for dataset files. I need to change it to be a parameter.
    // The default is set to either khaled's or Semih's machine for easy debug.
    //
    //From Semih's Machine:
    //private static String DEFAULT_ARG_BASE_DIR = "/Users/semihsalihoglu/Desktop/research/waterloo/graphflow/github/datasets/";
    public static String DEFAULT_ARG_SEED_EDGE_WEIGHT = "8881453";
    public static String ARG_EDGE_WEIGHT_RANGE = "edgeWeightRange";
    public static String DEFAULT_ARG_EDGE_WEIGHT_RANGE = "10";
    private static String ARG_BASE_DIR = "baseDir";
    //From Khaled's machine
    private static String DEFAULT_ARG_BASE_DIR = "/Users/khaledammar/Documents/GitHub/data/";
    private static String ARG_GRAPH_FILE_TO_LOAD = "graphFileToLoad";
    private static String DEFAULT_ARG_GRAPH_FILE_TO_LOAD = "soc-Epinions1-90.txt";
    // This value could be 1 or -1
    // 1 for always positive edges
    // -1 for a mix of positive/negative edges
    private static String ARG_EDGE_WEIGHT_SIGN = "edgeWeightSign";
    private static String DEFAULT_ARG_EDGE_WEIGHT_SIGN = "2";


    private String baseDir = "";
    private String graphFileToLoad = "";
    private Random edgeWeightGenerator;
    private int edgeWeightSeed;
    private int edgeWeightRange;
    private int edgeWeightSign; // 0 // 1 or -1 // 2 or -2 for positive and negative natural numbers

    GenerateWeightedGraphs(int edgeWeightRange, int edgeWeightSign) {

        this(Integer.valueOf(DEFAULT_ARG_SEED_EDGE_WEIGHT), edgeWeightRange, edgeWeightSign);
    }


    GenerateWeightedGraphs(int edgeWeightSeed, int edgeWeightRange, int edgeWeightSign) {
        this.edgeWeightSeed = edgeWeightSeed;
        this.edgeWeightGenerator = new Random(this.edgeWeightSeed);
        this.edgeWeightRange = edgeWeightRange;
        this.edgeWeightSign = edgeWeightSign;
    }

    GenerateWeightedGraphs(String baseDir, String graphFileToLoad, int edgeWeightSeed, int edgeWeightRange,
                           int edgeWeightSign) {
        this.baseDir = baseDir;
        this.graphFileToLoad = graphFileToLoad;
        this.edgeWeightSeed = edgeWeightSeed;
        this.edgeWeightGenerator = new Random(edgeWeightSeed);
        this.edgeWeightRange = edgeWeightRange;
        this.edgeWeightSign = edgeWeightSign;
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

        // Parse command line options
        CommandLine cmd = new DefaultParser().parse(getCommandLineOptions(), args);

        String baseDir = cmd.getOptionValue(ARG_BASE_DIR, DEFAULT_ARG_BASE_DIR);
        String graphFileToLoad = cmd.getOptionValue(ARG_GRAPH_FILE_TO_LOAD, DEFAULT_ARG_GRAPH_FILE_TO_LOAD);
        int edgeWeightSeed = Integer.parseInt(cmd.getOptionValue(ARG_SEED_EDGE_WEIGHT, DEFAULT_ARG_SEED_EDGE_WEIGHT));
        int edgeWeightRange =
                Integer.parseInt(cmd.getOptionValue(ARG_EDGE_WEIGHT_RANGE, DEFAULT_ARG_EDGE_WEIGHT_RANGE));
        int edgeWeightSign = Integer.parseInt(cmd.getOptionValue(ARG_EDGE_WEIGHT_SIGN, DEFAULT_ARG_EDGE_WEIGHT_SIGN));

        GenerateWeightedGraphs gen =
                new GenerateWeightedGraphs(baseDir, graphFileToLoad, edgeWeightSeed, edgeWeightRange, edgeWeightSign);

        long beginTime = System.nanoTime();

        gen.createWeightedGraph();

        long endTime = System.nanoTime();
        System.out.println("Total time : " + (endTime - beginTime) / 1000000000.0 + " seconds");
    }

    private static Options getCommandLineOptions() {
        Options options = new Options();
        options.addOption("help", false, "print this message");
        options.addOption(ARG_BASE_DIR, ARG_BASE_DIR, true, "base directory where the experimental " + "datasets are");
        options.addOption(ARG_GRAPH_FILE_TO_LOAD, ARG_GRAPH_FILE_TO_LOAD, true,
                "graph file to load and " + "deserialize");

        options.addOption(ARG_SEED_EDGE_WEIGHT, ARG_SEED_EDGE_WEIGHT, true,
                "The seed to use " + "for choosing random weights");
        options.addOption(ARG_EDGE_WEIGHT_RANGE, ARG_EDGE_WEIGHT_RANGE, true, "range of edge weights");

        options.addOption(ARG_EDGE_WEIGHT_SIGN, ARG_EDGE_WEIGHT_SIGN, true,
                "sign of edge generation 0, +1, -1, +2 , -2 (for natural numbers)");

        return options;
    }

    public float generateEdgeWeight() {

        int localEdgeWeightSign = edgeWeightSign;

        float weight = 1;
        // guaranteed positive edge
        // for edgeWeihtSign = 0 --> Always 1
        //                   = 1 --> For posiThe weights are always positive
        //                   = -1 --> The weights are most probably nagative

        if (Math.abs(localEdgeWeightSign) != 0) {
            // This means the weights are not 1
            if (Math.abs(localEdgeWeightSign) == 1) {
                // float weights
                weight += edgeWeightGenerator.nextInt(edgeWeightRange) / 2.0 +
                        localEdgeWeightSign * edgeWeightGenerator.nextDouble() *
                                edgeWeightGenerator.nextInt(edgeWeightRange) / 2.0;

                //                System.out.println("float -> "+ weight);
            } else {
                // int weights
                assert Math.abs(localEdgeWeightSign) == 2;

                localEdgeWeightSign = localEdgeWeightSign / 2;
                weight += edgeWeightGenerator.nextInt(edgeWeightRange) / 2 +
                        localEdgeWeightSign * edgeWeightGenerator.nextInt(edgeWeightRange) / 2;

                //                System.out.println("int -> "+ weight);
            }
        }

        if (weight == 0) {
            return generateEdgeWeight();
        } else {
            //            System.out.println(weight);
            return weight;
        }
    }

    private void createWeightedGraph() throws NumberFormatException, IOException {
        // get graph file

        String outfileName = this.graphFileToLoad + "_weighted_" + this.edgeWeightSign + "-" + this.edgeWeightRange;
        InputStreamReader in =
                new InputStreamReader(new FileInputStream(new File(this.baseDir + this.graphFileToLoad)));
        OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(new File(this.baseDir + outfileName)));
        BufferedReader buffer = new BufferedReader(in);
        BufferedWriter outBuffer = new BufferedWriter(out);

        String line;
        int edgesCount = 0;

        // Start Timer
        long beginTime = System.nanoTime();

        while ((line = buffer.readLine()) != null) {
            if (line.isEmpty()) {
                continue;
            }

            String[] parts = line.split("\t");

            int fromVertex = Integer.parseInt(parts[0]);
            int toVertex = Integer.parseInt(parts[1]);
            float weight = generateEdgeWeight();

            String outputLine = "" + fromVertex + "\t" + toVertex + "\t" + weight + "\n";
            //System.out.print(outputLine);
            outBuffer.write(outputLine);
            edgesCount++;
            if (edgesCount % 100000 == 0) {
                long t2 = System.nanoTime();
                System.out.println(edgesCount + " created in " + (t2 - beginTime) / 1000000.0);
                beginTime = t2;
            }
        }

        System.out.println("Initial Edges prepared and added\t" + edgesCount);
        System.out.println("New file name:\t" + baseDir + outfileName);
        buffer.close();
        outBuffer.close();
    }
}
