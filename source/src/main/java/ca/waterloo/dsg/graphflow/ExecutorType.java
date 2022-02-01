package ca.waterloo.dsg.graphflow;

import java.util.EnumSet;

public enum ExecutorType {
    // SPSP
    SPSP_W_DC,
    SPSP_W_DC_JOD,
    SPSP_W_BASELINE,
    SPSP_W_CDD,
    SPSP_W_CDD_DET,
    SPSP_W_CDD_PROB,

    // KHOP
    KHOP_DC,
    KHOP_DC_JOD,
    KHOP_BASELINE,
    KHOP_CDD,
    KHOP_CDD_DET,
    KHOP_CDD_PROB,

    // Q1
    Q1_DC,
    Q1_DC_JOD,
    Q1_BASELINE,
    Q1_CDD,
    Q1_CDD_DET,
    Q1_CDD_PROB,

    // Q2
    Q2_DC,
    Q2_DC_JOD,
    Q2_BASELINE,
    Q2_CDD,
    Q2_CDD_DET,
    Q2_CDD_PROB,

    // Q7
    Q7_DC,
    Q7_DC_JOD,
    Q7_BASELINE,
    Q7_CDD,
    Q7_CDD_DET,
    Q7_CDD_PROB,

    // Q11
    Q11_DC,
    Q11_DC_JOD,
    Q11_BASELINE,
    Q11_CDD,
    Q11_CDD_DET,
    Q11_CDD_PROB,

    UNW_BASELINE,
    UNI_UNWEIGHTED_DIFF_BFS,
    UNI_UNWEIGHTED_DIFF_BFS_BLOOM,
    UNI_UNWEIGHTED_DIFF_BFS_HASH,

    W_DIJKSTRA,
    UNW_DIJKSTRA,
    BIDIR_UNWEIGHTED_DIFF_BFS,
    UNIDIR_WEIGHTED_DIFF_BFS_UNREACHABLE,
    UNIDIR_WEIGHTED_DIFF_BFS_DROP_RANDOM_VERTEX,
    UNIDIR_POSITIVE_WEIGHTED_DIFF_BFS,
    VARIABLE_LENGTH_PATH,
    NEW_DIJKSTRA,
    OPT_DIJKSTRA,
    LANDMARK_DIFF,
    OPTIMIZED_BIDIR_UNWEIGHTED_DIFF_BFS;

    private static final EnumSet<ExecutorType> weighted =
            EnumSet.of(SPSP_W_BASELINE, SPSP_W_DC, SPSP_W_DC_JOD, SPSP_W_CDD, SPSP_W_CDD_DET, SPSP_W_CDD_PROB,
                    UNIDIR_WEIGHTED_DIFF_BFS_UNREACHABLE, UNIDIR_WEIGHTED_DIFF_BFS_DROP_RANDOM_VERTEX,
                    UNIDIR_POSITIVE_WEIGHTED_DIFF_BFS, W_DIJKSTRA);
    private static final EnumSet<ExecutorType> dc =
            EnumSet.of(SPSP_W_DC, KHOP_DC, Q1_DC, Q2_DC, Q7_DC, Q11_DC, SPSP_W_DC_JOD, KHOP_DC_JOD, Q1_DC_JOD,
                    Q2_DC_JOD, Q7_DC_JOD, Q11_DC_JOD);
    private static final EnumSet<ExecutorType> rpq =
            EnumSet.of(Q1_BASELINE, Q1_CDD, Q1_CDD_DET, Q1_CDD_PROB, Q1_DC, Q1_DC_JOD, Q2_BASELINE, Q2_CDD, Q2_CDD_DET,
                    Q2_CDD_PROB, Q2_DC, Q2_DC_JOD, Q7_CDD, Q7_BASELINE, Q7_CDD_PROB, Q7_CDD_DET, Q7_DC_JOD, Q7_DC,
                    Q11_CDD, Q11_BASELINE, Q11_CDD_PROB, Q11_CDD_DET, Q11_DC_JOD, Q11_DC);
    private static final EnumSet<ExecutorType> baseline =
            EnumSet.of(ExecutorType.UNW_BASELINE, ExecutorType.SPSP_W_BASELINE, ExecutorType.KHOP_BASELINE,
                    ExecutorType.Q1_BASELINE, ExecutorType.Q2_BASELINE, ExecutorType.Q7_BASELINE,
                    ExecutorType.Q11_BASELINE, ExecutorType.OPT_DIJKSTRA, ExecutorType.NEW_DIJKSTRA,
                    ExecutorType.UNW_DIJKSTRA, ExecutorType.W_DIJKSTRA);
    private static final EnumSet<ExecutorType> prob =
            EnumSet.of(ExecutorType.SPSP_W_CDD_PROB, ExecutorType.KHOP_CDD_PROB, ExecutorType.Q1_CDD_PROB,
                    ExecutorType.Q2_CDD_PROB, ExecutorType.Q7_CDD_PROB, ExecutorType.Q11_CDD_PROB);
    private static final EnumSet<ExecutorType> det =
            EnumSet.of(ExecutorType.SPSP_W_CDD_DET, ExecutorType.KHOP_CDD_DET, ExecutorType.Q1_CDD_DET,
                    ExecutorType.Q2_CDD_DET, ExecutorType.Q7_CDD_DET, ExecutorType.Q11_CDD_DET);

    public static ExecutorType getFromCommandLineName(String commandLineName) {
        try {
            return ExecutorType.valueOf(commandLineName.toUpperCase());
        } catch (IllegalArgumentException e) {
            switch (commandLineName) {
                case "baseline":
                case "unw-baseline":
                    return UNW_BASELINE;
                case "unw-baseline-khop":
                    return KHOP_BASELINE;
                case "Q1-baseline":
                    return Q1_BASELINE;
                case "Q2-baseline":
                    return Q2_BASELINE;
                case "Q7-baseline":
                    return Q7_BASELINE;
                case "Q11-baseline":
                    return Q11_BASELINE;
                case "w-baseline":
                    return SPSP_W_BASELINE;
                case "uni-unw-diff-bfs":
                    return UNI_UNWEIGHTED_DIFF_BFS;
                case "uni-unw-diff-bfs-bloom":
                    return UNI_UNWEIGHTED_DIFF_BFS_BLOOM;
                case "uni-unw-diff-bfs-hash":
                    return UNI_UNWEIGHTED_DIFF_BFS_HASH;
                case "optimized-bidir-unw-diff-bfs":
                    return OPTIMIZED_BIDIR_UNWEIGHTED_DIFF_BFS;
                case "bidir-unw-diff-bfs":
                    return BIDIR_UNWEIGHTED_DIFF_BFS;
                case "uni-w-diff-bfs-unreachable":
                    return UNIDIR_WEIGHTED_DIFF_BFS_UNREACHABLE;
                case "uni-w-diff-bfs":
                    return SPSP_W_CDD;
                case "uni-diff-khop":
                    return KHOP_CDD;
                case "Q1-diff":
                    return Q1_CDD;
                case "Q2-diff":
                    return Q2_CDD;
                case "Q7-diff":
                    return Q7_CDD;
                case "Q11-diff":
                    return Q11_CDD;
                case "uni-diff-khop-bloom":
                    return KHOP_CDD_PROB;
                case "uni-diff-khop-hash":
                    return KHOP_CDD_DET;
                case "uni-w-diff-bfs-bloom":
                    return SPSP_W_CDD_PROB;
                case "uni-w-diff-bfs-hash":
                    return SPSP_W_CDD_DET;
                case "Q1-drop":
                    return Q1_CDD_PROB;
                case "Q2-drop":
                    return Q2_CDD_PROB;
                case "Q7-drop":
                    return Q7_CDD_PROB;
                case "Q11-drop":
                    return Q11_CDD_PROB;
                case "Q1-drop-hash":
                    return Q1_CDD_DET;
                case "Q2-drop-hash":
                    return Q2_CDD_DET;
                case "Q7-drop-hash":
                    return Q7_CDD_DET;
                case "Q11-drop-hash":
                    return Q11_CDD_DET;
                case "uni-w-diff-bfs-dropRandom":
                    return UNIDIR_WEIGHTED_DIFF_BFS_DROP_RANDOM_VERTEX;
                case "uni-posw-diff-bfs":
                    return UNIDIR_POSITIVE_WEIGHTED_DIFF_BFS;
                case "var-len-diff-bfs":
                    return VARIABLE_LENGTH_PATH;
                case "unw-dijkstra":
                    return UNW_DIJKSTRA;
                case "w-dijkstra":
                    return W_DIJKSTRA;
                case "opt-dijkstra":
                    return OPT_DIJKSTRA;
                case "new-dijkstra":
                    return NEW_DIJKSTRA;
                case "landmark-diff":
                    return LANDMARK_DIFF;
                default:
                    throw new IllegalArgumentException("Unknown continuous sp executor type: " + commandLineName);
            }
        }
    }

    public static boolean isWeighted(ExecutorType executorType) {
        return weighted.contains(executorType);
    }

    public static boolean isDC(ExecutorType executorType) {
        return dc.contains(executorType);
    }

    public static boolean isRPQ(ExecutorType executorType) {
        return rpq.contains(executorType);
    }

    public static boolean isBaseLine(ExecutorType executorType) {
        return baseline.contains(executorType);
    }

    public static boolean isProb(ExecutorType executorType) {
        return prob.contains(executorType);
    }

    public static boolean isDetOrProb(ExecutorType executorType) {
        return prob.contains(executorType) || det.contains(executorType);
    }

    // TODO: Doublec check getWeightSign - why POS lead to -1?
    // decide the generated edge weight sign based on the execution type
    public static int getWeightSign(ExecutorType exec) {
        switch (exec) {
            case UNW_BASELINE:
            case UNI_UNWEIGHTED_DIFF_BFS:
            case BIDIR_UNWEIGHTED_DIFF_BFS:
            case VARIABLE_LENGTH_PATH:
            case UNW_DIJKSTRA:
                return 0;

            case SPSP_W_BASELINE:
            case W_DIJKSTRA:
            case SPSP_W_CDD:
            case UNI_UNWEIGHTED_DIFF_BFS_BLOOM:
            case UNI_UNWEIGHTED_DIFF_BFS_HASH:
            case KHOP_CDD:
            case UNIDIR_WEIGHTED_DIFF_BFS_UNREACHABLE:
            case SPSP_W_CDD_PROB:
            case SPSP_W_CDD_DET:
            case UNIDIR_WEIGHTED_DIFF_BFS_DROP_RANDOM_VERTEX:
                return 1;

            case UNIDIR_POSITIVE_WEIGHTED_DIFF_BFS:
                return -1;

            default:
                return 0;
        }
    }
}
