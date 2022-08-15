package ca.waterloo.dsg.graphflow.query.planner;

import ca.waterloo.dsg.graphflow.exception.LabelOrTypeNotFoundException;
import ca.waterloo.dsg.graphflow.graph.EdgeStore;
import ca.waterloo.dsg.graphflow.graph.Graph;
import ca.waterloo.dsg.graphflow.graph.SortedAdjacencyList;
import ca.waterloo.dsg.graphflow.graph.TypeAndPropertyKeyStore;
import ca.waterloo.dsg.graphflow.graph.VertexPropertyStore;
import ca.waterloo.dsg.graphflow.query.plan.QueryPlan;
import ca.waterloo.dsg.graphflow.query.result.AbstractQueryResult;
import ca.waterloo.dsg.graphflow.query.result.Message;
import ca.waterloo.dsg.graphflow.query.structuredquery.StructuredQuery;
import ca.waterloo.dsg.graphflow.query.structuredquery.StructuredQuery.QueryOperation;
import ca.waterloo.dsg.graphflow.util.datatype.DataType;
import org.antlr.v4.runtime.misc.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Create a {@code QueryPlan} for the LOAD FROM CSV operation.
 */
public class LoadDataFromCSVQueryPlanner extends AbstractQueryPlanner {

    private final static String ID = "ID";
    private final static String START_ID = "START_ID";
    private final static String END_ID = "END_ID";
    // TYPE and LABEL are used for edge and vertex types, respectively, to maintain compatibility
    // with other csv loaders and enable loading the same csv files into Graphflow.
    private final static String TYPE = "TYPE";
    private final static String LABEL = "LABEL";
    private final static String COLON = ":";

    private String dataLoadedType;

    public LoadDataFromCSVQueryPlanner(StructuredQuery structuredQuery) {
        super(structuredQuery);
        dataLoadedType = structuredQuery.getQueryOperation() == QueryOperation.LOAD_CSV_VERTICES ?
            "vertices" : "edges";
    }

    @Override
    public QueryPlan plan() {
        return new QueryPlan() {
            @Override
            public AbstractQueryResult execute() {
                try {
                    if (structuredQuery.getQueryOperation() == QueryOperation.LOAD_CSV_VERTICES) {
                        loadVertices();
                    } else {
                        loadEdges();
                    }
                    return new Message(String.format("Graph %s loaded from directory '%s'.",
                        dataLoadedType, structuredQuery.getFilePath()));
                } catch (LabelOrTypeNotFoundException e) {
                    return new Message(String.format(e.getMessage(), structuredQuery.getFilePath()),
                        true /* isError */);
                } catch (Exception e) {
                    return new Message(String.format("Error loading graph %s from '%s': %s[%s]",
                        dataLoadedType, structuredQuery.getFilePath(), e.getClass().
                            getCanonicalName(), e.getMessage()), true /* isError */);
                }
            }
        };
    }

    /**
     * Loads vertices from a CSV file into the Graph.
     * <p>
     * The header is of the form `id:ID,[:LABEL],[property:TYPE]` (in any order), and the rows
     * contain the corresponding data. The TYPE is STRING by default, and mentioning `id` means
     * adding it as a property of type INT.
     * <p>
     * Having a name on the left hand side of the ID header assigns the ID to a property of the
     * vertex. Having a name on the left hand side of the LABEL header does nothing.
     * <p>
     * eg. id:ID,:LABEL,name:STRING,age:INT
     *
     * @throws IOException if I/O errors happen given the file path provided by the user.
     */
    private void loadVertices() throws IOException {
        BufferedReader vertexReader = new BufferedReader(new FileReader(structuredQuery.
            getFilePath()));

        int idIndex = -1;
        int typeIndex = -1;
        String[] header = vertexReader.readLine().split(structuredQuery.getCsvSeparator(),
            -1 /* retain empty strings after splitting */);
        Map<Integer, Pair<Short, DataType>> vertexPropertiesIndexMap = new HashMap<>();
        for (int i = 0; i < header.length; ++i) {
            String[] columnSplit = header[i].split(COLON);
            if (columnSplit.length == 2 && columnSplit[1].toUpperCase().equals(ID)) {
                idIndex = i;
                if (!columnSplit[0].isEmpty()) {
                    vertexPropertiesIndexMap.put(i, TypeAndPropertyKeyStore.getInstance().
                        mapStringPropertyKeyValueToShortAndDataTypeOrInsert(columnSplit[0],
                            DataType.INT.name()));
                }
            } else if (columnSplit.length == 2 && columnSplit[1].toUpperCase().equals(LABEL)) {
                typeIndex = i;
            } else {
                String propertyName = columnSplit[0];
                String PropertyDataType = columnSplit.length < 2 ? DataType.STRING.name() :
                    columnSplit[1];
                Pair<Short, DataType> propertyShortAndDataType = TypeAndPropertyKeyStore.
                    getInstance().mapStringPropertyKeyValueToShortAndDataTypeOrInsert(propertyName,
                    PropertyDataType);
                vertexPropertiesIndexMap.put(i, propertyShortAndDataType);
            }
        }

        if (-1 == typeIndex && null == structuredQuery.getCsvDataType()) {
            throw new LabelOrTypeNotFoundException("The file does not contain the vertex " +
                "labels and the user did not provide one.");
        }

        int maxVertexId = Graph.getInstance().getHighestPermanentVertexId();
        String vertex = vertexReader.readLine();
        while (null != vertex) {
            String[] vertexData = vertex.split(structuredQuery.getCsvSeparator(),
                -1 /* retain empty strings after splitting */);
            int vertexId = Integer.parseInt(vertexData[idIndex]);
            if (vertexId > maxVertexId) {
                maxVertexId = vertexId;
                Graph.getInstance().ensureCapacity(maxVertexId + 1);
            }

            String type = typeIndex != -1 ? vertexData[typeIndex] : structuredQuery.
                getCsvDataType();
            Graph.getInstance().setVertexType(vertexId, TypeAndPropertyKeyStore.getInstance().
                mapStringTypeToShortOrInsert(type));

            Map<Short, Pair<DataType, String>> mappedProperties = new HashMap<>();
            for (Map.Entry<Integer, Pair<Short, DataType>> entry : vertexPropertiesIndexMap.
                entrySet()) {
                mappedProperties.put(entry.getValue().a, new Pair<>(entry.getValue().b,
                    vertexData[entry.getKey()]));
            }
            VertexPropertyStore.getInstance().set(vertexId, mappedProperties);
            vertex = vertexReader.readLine();
        }
        Graph.getInstance().setHighestPermanentAndMergedVertexId(maxVertexId);
    }

    /**
     * Loads edges from a CSV into the graph.
     * <p>
     * The CSV header is of the form `:START_ID,:END_ID,[:TYPE],[property:TYPE] (in any order) and
     * the rows contain the corresponding data. Property types are STRING by default.
     * Having a value on the left hand side of the START_ID, END_ID and TYPE headers does nothing.
     * <p>
     * eg. :START_ID,:END_ID,:TYPE,name:STRING,value:INT
     *
     * @throws IOException if I/O errors happen given the file path provided by the user.
     */
    private void loadEdges() throws IOException {
        BufferedReader edgeReader = new BufferedReader(new FileReader(structuredQuery.
            getFilePath()));
        int fromVertexIndex = -1;
        int toVertexIndex = -1;
        int typeIndex = -1;
        String[] header = edgeReader.readLine().split(structuredQuery.getCsvSeparator(),
            -1 /* retain empty strings after splitting */);
        Map<Integer, Pair<Short, DataType>> edgePropertiesIndexMap = new HashMap<>();
        for (int i = 0; i < header.length; ++i) {
            String[] columnSplit = header[i].split(COLON);
            if (columnSplit.length == 2 && columnSplit[1].toUpperCase().equals(START_ID)) {
                fromVertexIndex = i;
            } else if (columnSplit.length == 2 && columnSplit[1].toUpperCase().equals(END_ID)) {
                toVertexIndex = i;
            } else if (columnSplit.length == 2 && columnSplit[1].toUpperCase().equals(TYPE)) {
                typeIndex = i;
            } else {
                String propertyName = columnSplit[0];
                String PropertyDataType = columnSplit.length < 2 ? DataType.STRING.name() :
                    columnSplit[1];
                Pair<Short, DataType> propertyShortAndDataType = TypeAndPropertyKeyStore.
                    getInstance().mapStringPropertyKeyValueToShortAndDataTypeOrInsert(
                    propertyName, PropertyDataType);
                edgePropertiesIndexMap.put(i, propertyShortAndDataType);
            }
        }

        if (-1 == typeIndex && null == structuredQuery.getCsvDataType()) {
            throw new LabelOrTypeNotFoundException("The file does not contain the edge types and " +
                "the user did not provide one.");
        }

        SortedAdjacencyList[] forwardLists = Graph.getInstance().getForwardAdjLists();
        SortedAdjacencyList[] backwardLists = Graph.getInstance().getBackwardAdjLists();
        String edge = edgeReader.readLine();
        while (null != edge) {
            String[] edgeData = edge.split(structuredQuery.getCsvSeparator(),
                -1 /* retain empty strings after splitting */);
            Map<Short, Pair<DataType, String>> mappedProperties = new HashMap<>();
            for (Map.Entry<Integer, Pair<Short, DataType>> entry : edgePropertiesIndexMap.
                entrySet()) {
                mappedProperties.put(entry.getValue().a, new Pair<>(entry.getValue().b,
                    edgeData[entry.getKey()]));
            }
            long edgeId = EdgeStore.getInstance().addEdge(mappedProperties);
            int fromId = Integer.parseInt(edgeData[fromVertexIndex]);
            int toId = Integer.parseInt(edgeData[toVertexIndex]);
            String type = typeIndex != -1 ? edgeData[typeIndex] : structuredQuery.getCsvDataType();
            short edgeType = TypeAndPropertyKeyStore.getInstance().mapStringTypeToShortOrInsert(
                type);
            forwardLists[fromId].append(toId, edgeType, edgeId);
            backwardLists[toId].append(fromId, edgeType, edgeId);
            edge = edgeReader.readLine();
        }
        for (int i = 0; i < forwardLists.length; ++i) {
            if (null != forwardLists[i]) {
                forwardLists[i].sort();
                backwardLists[i].sort();
            }
        }
    }
}
