package ca.waterloo.dsg.graphflow.query.parser;

import ca.waterloo.dsg.graphflow.TestUtils;
import ca.waterloo.dsg.graphflow.query.structuredquery.StructuredQuery;
import ca.waterloo.dsg.graphflow.query.structuredquery.StructuredQuery.QueryOperation;
import org.junit.Test;

/**
 * Tests {@link StructuredQueryParser} for CSV loading
 */
public class LoadCSVTest {

    @Test
    public void testLoadVerticesWithoutLabel() throws Exception {
        String query = "LOAD VERTICES FROM CSV 'testVerticesNoLabel.csv' SEPARATOR ';';";
        StructuredQuery actualStructuredQuery = new StructuredQueryParser().parse(query);

        StructuredQuery expectedStructuredQuery = new StructuredQuery();
        expectedStructuredQuery.setQueryOperation(QueryOperation.LOAD_CSV_VERTICES);
        expectedStructuredQuery.setFilePath("testVerticesNoLabel.csv");
        expectedStructuredQuery.setCsvSeparator(";");
        TestUtils.assertEquals(expectedStructuredQuery, actualStructuredQuery);
    }

    @Test
    public void testLoadVerticesWithLabel() throws Exception {
        String query = "LOAD VERTICES WITH LABEL 'name' FROM CSV 'testVertices.csv' SEPARATOR ',';";
        StructuredQuery actualStructuredQuery = new StructuredQueryParser().parse(query);

        StructuredQuery expectedStructuredQuery = new StructuredQuery();
        expectedStructuredQuery.setQueryOperation(QueryOperation.LOAD_CSV_VERTICES);
        expectedStructuredQuery.setFilePath("testVertices.csv");
        expectedStructuredQuery.setCsvSeparator(",");
        expectedStructuredQuery.setCsvDataType("name");
        TestUtils.assertEquals(expectedStructuredQuery, actualStructuredQuery);
    }

    @Test
    public void testLoadEdgesWithoutType() throws Exception {
        String query = "LOAD EDGES FROM CSV 'testEdges.csv' SEPARATOR '-';";
        StructuredQuery actualStructuredQuery = new StructuredQueryParser().parse(query);

        StructuredQuery expectedStructuredQuery = new StructuredQuery();
        expectedStructuredQuery.setQueryOperation(QueryOperation.LOAD_CSV_EDGES);
        expectedStructuredQuery.setFilePath("testEdges.csv");
        expectedStructuredQuery.setCsvSeparator("-");
        TestUtils.assertEquals(expectedStructuredQuery, actualStructuredQuery);
    }

    @Test
    public void testLoadEdgesWithType() throws Exception {
        String query = "LOAD EDGES WITH TYPE 'friend' FROM CSV 'testEdgesWithType.csv' SEPARATOR " +
            "'.';";
        StructuredQuery actualStructuredQuery = new StructuredQueryParser().parse(query);

        StructuredQuery expectedStructuredQuery = new StructuredQuery();
        expectedStructuredQuery.setQueryOperation(QueryOperation.LOAD_CSV_EDGES);
        expectedStructuredQuery.setFilePath("testEdgesWithType.csv");
        expectedStructuredQuery.setCsvSeparator(".");
        expectedStructuredQuery.setCsvDataType("friend");
        TestUtils.assertEquals(expectedStructuredQuery, actualStructuredQuery);
    }
}
