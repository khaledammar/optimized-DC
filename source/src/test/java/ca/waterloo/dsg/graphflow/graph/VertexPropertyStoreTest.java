package ca.waterloo.dsg.graphflow.graph;

import ca.waterloo.dsg.graphflow.util.datatype.DataType;
import org.antlr.v4.runtime.misc.Pair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Tests {@link VertexPropertyStore}.
 */
public class VertexPropertyStoreTest {

    @Before
    public void resetGraphDB() {
        GraphDBState.reset();
    }

    @Test
    public void testSetAndGetMethods() {
        TypeAndPropertyKeyStore typeAndPropertyKeyStore = TypeAndPropertyKeyStore.getInstance();
        VertexPropertyStore vertexPropertyStore = VertexPropertyStore.getInstance();

        Map<String, Pair<String, String>> keysToAddToPropertyStore = new LinkedHashMap<>();
        keysToAddToPropertyStore.put("Team Name", new Pair<>("String", "Real Madrid C.F."));
        keysToAddToPropertyStore.put("UEFA Champions League Wins", new Pair<>("int", "11"));
        keysToAddToPropertyStore.put("Won Champions League 15-16", new Pair<>("boolean", "true"));
        keysToAddToPropertyStore.put("Debt in M€ end 09-10", new Pair<>("double", "244.6"));

        Map<Short, Pair<DataType, String>> keyToDataTypeValuePairMap = typeAndPropertyKeyStore.
            mapStringPropertiesToShortAndDataTypeOrInsert(keysToAddToPropertyStore);
        Graph.getInstance().setHighestMergedVertexId(2);
        vertexPropertyStore.set(2, keyToDataTypeValuePairMap);

        Assert.assertEquals(null, vertexPropertyStore.vertexProperties[0]);
        Assert.assertEquals(null, vertexPropertyStore.vertexProperties[1]);
        Assert.assertEquals(2 /* short keys */ * 4 /* properties */ + 4 /* string length */ +
                4 /* 4 bytes for int */ + 8 /* bytes for double */ + 1 /* byte for boolean */ +
                "Real Madrid C.F.".length() /* UTF-8 conversion so each char maps to 1 byte */,
            vertexPropertyStore.vertexProperties[2].length);

        Map<Short, Object> vertexProperties = vertexPropertyStore.getProperties(2);
        Assert.assertEquals(4, vertexProperties.size());
        Assert.assertEquals("Real Madrid C.F.", vertexProperties.get((short) 1));
        Assert.assertEquals(11, vertexProperties.get((short) 2));
        Assert.assertEquals(true, vertexProperties.get((short) 3));
        Assert.assertEquals(244.6, vertexProperties.get((short) 4));
    }
}
