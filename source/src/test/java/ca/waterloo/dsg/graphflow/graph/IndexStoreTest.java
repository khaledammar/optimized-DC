package ca.waterloo.dsg.graphflow.graph;

import ca.waterloo.dsg.graphflow.exception.IndexAlreadyExistsException;
import ca.waterloo.dsg.graphflow.exception.NoSuchPropertyKeyException;
import ca.waterloo.dsg.graphflow.exception.UnindexableDataTypeException;
import ca.waterloo.dsg.graphflow.query.parser.StructuredQueryParser;
import ca.waterloo.dsg.graphflow.query.planner.CreateQueryPlanner;
import ca.waterloo.dsg.graphflow.query.planner.IndexCreationQueryPlanner;
import ca.waterloo.dsg.graphflow.query.structuredquery.StructuredQuery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Tests {@link IndexStore}.
 */
public class IndexStoreTest {

    @Rule
    public TemporaryFolder serFolder= new TemporaryFolder();

    @Before
    public void resetGraphDB() {
        GraphDBState.reset();
    }

    @Test
    public void testEndToEndIndexCreation() {
        createSimpleGraph();
        short personType = 1;
        short nameProperty = 1;

        Assert.assertFalse(IndexStore.getInstance().isIndexed(personType, nameProperty));
        createIndex(":(name)");
        Assert.assertTrue(IndexStore.getInstance().isIndexed(personType, nameProperty));
        Set<Integer> paulVertices = IndexStore.getInstance().getVertices(personType, nameProperty,
            "Paul");
        Assert.assertEquals(1, paulVertices.size());
        Assert.assertEquals(1, IndexStore.getInstance().numberOfPropertyTypes());
    }

    @Test(expected = NoSuchPropertyKeyException.class)
    public void testEndToEndInvalidPropertyIndexCreation() {
        createSimpleGraph();
        createIndex(":(invalidPropertyThatDoesntExist)");
    }

    @Test
    public void testSimpleIndex() {
        createComplexGraph();
        short dogType = 1;
        short personType = 2;
        short nameProperty = 1;

        Assert.assertFalse(IndexStore.getInstance().isIndexed(personType, nameProperty));
        createIndex(personType, nameProperty);
        Assert.assertTrue(IndexStore.getInstance().isIndexed(personType, nameProperty));
        Set<Integer> sids = IndexStore.getInstance().getVertices(personType, nameProperty,
            "Sid");
        Assert.assertEquals(sids.size(), 1);
        Assert.assertTrue(sids.contains(4));
        Assert.assertFalse(IndexStore.getInstance().isIndexed(dogType, nameProperty));
    }

    @Test
    public void testCreateIndexAllTypesIndex() {
        createComplexGraph();
        short dogType = 1;
        short personType = 2;
        short nameProperty = 1;
        Assert.assertFalse(IndexStore.getInstance().isIndexed(personType, nameProperty));
        createIndex(nameProperty);

        Assert.assertTrue(IndexStore.getInstance().isIndexed(personType, nameProperty));
        Set<Integer> sids = IndexStore.getInstance().getVertices(personType, nameProperty,
            "Sid");
        Assert.assertEquals(sids.size(), 1);
        Assert.assertTrue(sids.contains(4));
        Assert.assertTrue(IndexStore.getInstance().isIndexed(dogType, nameProperty));
        Set<Integer> oliviers = IndexStore.getInstance().getVertices(TypeAndPropertyKeyStore.ANY,
            nameProperty, "Olivier");
        Assert.assertEquals(oliviers.size(), 1);
        Assert.assertTrue(oliviers.contains(1));
    }

    @Test
    public void testGetVerticesOfManyTypes() {
        runCreate("CREATE (1:Person { name: 'Paul' }), (2:Dog { name: 'Paul' });");
        short nameProperty = 1;

        createIndex(nameProperty);
        Set<Integer> paulVertices = IndexStore.getInstance().getVertices(
            TypeAndPropertyKeyStore.ANY, nameProperty, "Paul");
        Assert.assertEquals(2, paulVertices.size());
        Assert.assertTrue(paulVertices.contains(1));
        Assert.assertTrue(paulVertices.contains(2));
    }

    @Test
    public void testChangeVertexPropertiesByEdgeCreation() {
        createSimpleGraph();
        short personType = 1;
        short nameProperty = 1;
        Assert.assertFalse(IndexStore.getInstance().isIndexed(personType, nameProperty));
        createIndex(nameProperty);

        Assert.assertTrue(IndexStore.getInstance().isIndexed(personType, nameProperty));
        Set<Integer> paulVertices = IndexStore.getInstance().getVertices(personType, nameProperty,
            "Paul");
        Assert.assertEquals(1, paulVertices.size());
        runCreate("CREATE (1:Person { name: 'Tom' })-[:friend]->(2:Person { name: " +
            "'Naren' });");

        Assert.assertTrue(IndexStore.getInstance().isIndexed(personType, nameProperty));
        paulVertices = IndexStore.getInstance().getVertices(personType, nameProperty,
            "Paul");
        Assert.assertEquals(0, paulVertices.size());
        Set<Integer> tomVertices = IndexStore.getInstance().getVertices(personType, nameProperty,
            "Tom");
        Assert.assertEquals(1, tomVertices.size());
    }

    @Test
    public void testRemoveVertexPropertiesByEdgeCreation() {
        createSimpleGraph();
        short personType = 1;
        short nameProperty = 1;
        createIndex(nameProperty);
        Assert.assertTrue(IndexStore.getInstance().isIndexed(personType, nameProperty));
        runCreate("CREATE (1:Person)-[:friend]->(2:Person { name: 'Naren' });");

        Set<Integer> paulVertices = IndexStore.getInstance().getVertices(personType, nameProperty,
            "Paul");
        Assert.assertEquals(1, paulVertices.size());
        Set<Integer> narenVertices = IndexStore.getInstance().getVertices(personType,
            nameProperty, "Naren");
        Assert.assertEquals(1, narenVertices.size());
    }

    @Test
    public void testChangeVertexPropertiesByVertexCreation() {
        createSimpleGraph();
        short personType = 1;
        short nameProperty = 1;
        createIndex(nameProperty);
        runCreate("CREATE (1:Person { name: 'Tom' });");

        Set<Integer> paulVertices = IndexStore.getInstance().getVertices(personType, nameProperty,
            "Paul");
        Assert.assertEquals(0, paulVertices.size());
        Set<Integer> tomVertices = IndexStore.getInstance().getVertices(personType, nameProperty,
            "Tom");
        Assert.assertEquals(1, tomVertices.size());
    }

    @Test
    public void testRemoveVertexPropertiesByVertexCreation() {
        createSimpleGraph();
        short personType = 1;
        short nameProperty = 1;
        createIndex(nameProperty);
        String nodeUpdateQuery = "CREATE (1:Person { name: 'Tom' });";
        StructuredQuery nodeUpdateStructuredQuery = new StructuredQueryParser().parse(
            nodeUpdateQuery);
        new CreateQueryPlanner(nodeUpdateStructuredQuery).plan().execute();

        Set<Integer> paulVertices = IndexStore.getInstance().getVertices(personType, nameProperty,
            "Paul");
        Assert.assertEquals(0, paulVertices.size());
        Set<Integer> narenVertices = IndexStore.getInstance().getVertices(personType, nameProperty,
            "Naren");
        Assert.assertEquals(1, narenVertices.size());
    }

    @Test
    public void testChangeVertexPropertyWhenIndexedOnASpecificType() {
        createSimpleGraphWithManyTypes();
        short personType = 1;
        short nameProperty = 1;
        createIndex(personType, nameProperty);
        runCreate("CREATE (1:Person { name: 'Tom' });");

        Set<Integer> paulVertices = IndexStore.getInstance().getVertices(personType, nameProperty,
            "Paul");
        Assert.assertEquals(0, paulVertices.size());
        Set<Integer> tomVertices = IndexStore.getInstance().getVertices(personType, nameProperty,
            "Tom");
        Assert.assertEquals(1, tomVertices.size());
    }

    @Test
    public void testIndexOnStringSplit() {
        createSimpleGraph();
        short personType = 1;
        short nameProperty = 1;
        createIndex(nameProperty);

        Set<Integer> paulVertices = IndexStore.getInstance().getVertices(personType, nameProperty,
            "Paul");
        Assert.assertEquals(1, paulVertices.size());
        Set<Integer> bardeaVertices = IndexStore.getInstance().getVertices(personType, nameProperty,
            "Bardea");
        Assert.assertEquals(1, bardeaVertices.size());
    }

    @Test
    public void testCreateVertexOfNewTypeThatShouldBeIndexed() {
        createSimpleGraph();
        short personType = 1;
        short nameProperty = 1;
        createIndex(nameProperty);

        Assert.assertTrue(IndexStore.getInstance().isIndexed(personType, nameProperty));
        Assert.assertEquals(1, IndexStore.getInstance().numberOfPropertyTypes());
        runCreate("CREATE (3:Dog { name: 'Fido' });");
        short dogType = 2;

        Assert.assertEquals(1, IndexStore.getInstance().numberOfPropertyTypes());
        Assert.assertTrue(IndexStore.getInstance().isIndexed(personType, nameProperty));
        Assert.assertTrue(IndexStore.getInstance().isIndexed(dogType, nameProperty));
        Set<Integer> fidoVertices = IndexStore.getInstance().getVertices(dogType, nameProperty,
            "Fido");
        Assert.assertEquals(1, fidoVertices.size());
    }

    @Test
    public void testIndexManyConcreteTypes() {
        createSimpleGraphWithManyTypes();
        short personType = 1;
        short dogType = 2;
        short catType = 3;
        short nameProperty = 1;

        createIndex(personType, nameProperty);
        Assert.assertEquals(1, IndexStore.getInstance().numberOfPropertyTypes());
        Assert.assertTrue(IndexStore.getInstance().isIndexed(personType, nameProperty));
        Assert.assertFalse(IndexStore.getInstance().isIndexed(dogType, nameProperty));
        Assert.assertFalse(IndexStore.getInstance().isIndexed(catType, nameProperty));

        createIndex(dogType, nameProperty);
        Assert.assertEquals(2, IndexStore.getInstance().numberOfPropertyTypes());
        Assert.assertTrue(IndexStore.getInstance().isIndexed(personType, nameProperty));
        Assert.assertTrue(IndexStore.getInstance().isIndexed(dogType, nameProperty));
        Assert.assertFalse(IndexStore.getInstance().isIndexed(catType, nameProperty));
    }

    @Test
    public void testExpandExistingIndexToAllTypes() {
        createSimpleGraphWithManyTypes();
        short personType = 1;
        short dogType = 2;
        short catType = 3;
        short nameProperty = 1;
        short ownerProperty = 2;

        createIndex(personType, nameProperty);
        createIndex(dogType, ownerProperty);
        Assert.assertTrue(IndexStore.getInstance().isIndexed(personType, nameProperty));
        Assert.assertFalse(IndexStore.getInstance().isIndexed(dogType, nameProperty));
        Assert.assertFalse(IndexStore.getInstance().isIndexed(catType, nameProperty));
        Assert.assertTrue(IndexStore.getInstance().isIndexed(dogType, ownerProperty));
        Assert.assertEquals(2, IndexStore.getInstance().numberOfPropertyTypes());

        createIndex(nameProperty);
        Assert.assertTrue(IndexStore.getInstance().isIndexed(personType, nameProperty));
        Assert.assertTrue(IndexStore.getInstance().isIndexed(dogType, nameProperty));
        Assert.assertTrue(IndexStore.getInstance().isIndexed(catType, nameProperty));
        Assert.assertTrue(IndexStore.getInstance().isIndexed(dogType, ownerProperty));
        Assert.assertEquals(2, IndexStore.getInstance().numberOfPropertyTypes());

        Set<Integer> paulVertices = IndexStore.getInstance().getVertices(personType, nameProperty,
            "Paul");
        Assert.assertEquals(1, paulVertices.size());
        Set<Integer> fidoVertices = IndexStore.getInstance().getVertices(
            TypeAndPropertyKeyStore.ANY, nameProperty, "Fido");
        Assert.assertEquals(1, fidoVertices.size());
        Set<Integer> lisaVertices = IndexStore.getInstance().getVertices(catType, nameProperty,
            "Lisa");
        Assert.assertEquals(1, lisaVertices.size());
        Set<Integer> bardeaVertices = IndexStore.getInstance().getVertices(
            TypeAndPropertyKeyStore.ANY, ownerProperty, "Bardea");
        Assert.assertEquals(1, bardeaVertices.size());
    }

    @Test(expected = UnindexableDataTypeException.class)
    public void testIndexNotString() {
        createGraphWithIntegers();
        short ageProperty = 1;
        createIndex(ageProperty);
    }

    @Test(expected = NoSuchPropertyKeyException.class)
    public void testCreatingIndexOnNonexistentProperty() {
        createSimpleGraph();
        short invalidProperty = 123;
        createIndex(invalidProperty);
    }

    @Test(expected = IndexAlreadyExistsException.class)
    public void testCreateRedundantIndexForAllTypesThenSpecificType() {
        createSimpleGraph();
        short personType = 1;
        short nameProperty = 1;

        createIndex(nameProperty);
        createIndex(personType, nameProperty);
    }

    @Test(expected = IndexAlreadyExistsException.class)
    public void testCreateDuplicateTypedIndex() {
        createSimpleGraph();
        short personType = 1;
        short nameProperty = 1;

        createIndex(personType, nameProperty);
        createIndex(personType, nameProperty);
    }

    @Test(expected = IndexAlreadyExistsException.class)
    public void testCreateDuplicateUntypedIndex() {
        createSimpleGraph();
        short nameProperty = 1;

        createIndex(nameProperty);
        createIndex(nameProperty);
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        createSimpleGraphWithManyTypes();
        short personType = 1;
        short catType = 3;
        short nameProperty = 1;
        short ageProperty = 3;
        createIndex(personType, nameProperty);
        createIndex(catType, nameProperty);
        createIndex(ageProperty);

        Map<Integer, Map<String, Set<Integer>>> oldIndecies = IndexStore.getInstance().indices;
        Map<Short, Set<Short>> oldIndexedPropertiesAndTypes = IndexStore.getInstance().
            indexedPropertiesAndTypes;
        IndexStore.getInstance().serializeAll(serFolder.getRoot().getAbsolutePath());
        IndexStore.reset();

        IndexStore.getInstance().deserializeAll(serFolder.getRoot().getAbsolutePath());
        Assert.assertEquals(oldIndecies, IndexStore.getInstance().indices);
        Assert.assertEquals(oldIndexedPropertiesAndTypes, IndexStore.getInstance().
            indexedPropertiesAndTypes);
    }

    private void createSimpleGraph() {
        String graphCreationQuery = "CREATE (1:Person { name: 'Paul Bardea' }), (2:Person { name:" +
            "'Naren' });";
        runCreate(graphCreationQuery);
    }

    private void createGraphWithIntegers() {
        String graphCreationQuery = "CREATE (1:Person { age: 12 }), (2:Person { age: 13 });";
        runCreate(graphCreationQuery);
    }

    private void createSimpleGraphWithManyTypes() {
        String graphCreationQuery = "CREATE (1:Person { name: 'Paul Bardea' }), (2:Person { name:" +
            "'Naren' }), (3:Dog { name: 'Fido', owner: 'Paul Bardea', age: 'four' }), (4:Cat { " +
            "name: 'Lisa', age: 'five' });";
        runCreate(graphCreationQuery);
    }

    private void createComplexGraph() {
        String graphCreationQuery = "CREATE " +
            "(1:Dog{ name: 'Olivier' })-[:FOLLOWS{ date:3 }]->(2:Person{ name: 'Mohannad'})," +
            "(1:Dog{ name: 'Olivier' })-[:LIKES{ date:2 }]->(2:Person{ name: 'Mohannad' })," +
            "(2:Person{ name: 'Mohannad' })-[:FOLLOWS]->(4:Person { name: 'Sid' })," +
            "(1:Dog{ name: 'Olivier' })-[:FOLLOWS]->(4:Person{ name: 'Sid' })," +
            "(4:Person{ name: 'Sid' })-[:LIKES]->(1:Dog{ name: 'Olivier' });";
        runCreate(graphCreationQuery);
    }

    private void createIndex(Short typeKey, Short propertyKey) {
        IndexStore.getInstance().createIndex(typeKey, propertyKey);
    }

    private void createIndex(Short propertyKey) {
        IndexStore.getInstance().createIndex(TypeAndPropertyKeyStore.ANY, propertyKey);
    }

    private void createIndex(String indexString) {
        String indexCreationQuery = "CREATE INDEX ON " + indexString + ";";
        new IndexCreationQueryPlanner(new StructuredQueryParser().parse(indexCreationQuery)).plan().
            execute();
    }

    private void runCreate(String createQuery) {
        new CreateQueryPlanner(new StructuredQueryParser().parse(createQuery)).plan().execute();
    }
}
