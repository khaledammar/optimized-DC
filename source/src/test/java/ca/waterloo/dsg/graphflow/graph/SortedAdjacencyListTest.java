package ca.waterloo.dsg.graphflow.graph;

import ca.waterloo.dsg.graphflow.util.IntArrayList;
import ca.waterloo.dsg.graphflow.util.ShortArrayList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

/**
 * Tests {@link SortedAdjacencyList}.
 */
public class SortedAdjacencyListTest {

    @Before
    public void setUp() {
        GraphDBState.reset();
    }

    private SortedAdjacencyList getPopulatedAdjacencyList(int[] neighbourIds, short[] neighbourTypes,
                                                          long[] neighbourEdgeIds) {
        SortedAdjacencyList adjacencyList = new SortedAdjacencyList();
        for (int i = 0; i < neighbourIds.length; i++) {
            if (neighbourTypes != null && neighbourTypes.length == neighbourIds.length) {
                adjacencyList.add(neighbourIds[i], neighbourTypes[i], neighbourEdgeIds[i]);
            }
        }
        return adjacencyList;
    }

    private void testSort(int[] inputNeighbourIds, short[] inputNeighbourTypes, long[] inputNeighbourEdgeIds,
                          int[] sortedNeighbourIds, short[] sortedNeighbourTypes, long[] sortedNeighbourEdgeIds) {
        SortedAdjacencyList adjacencyList =
                getPopulatedAdjacencyList(inputNeighbourIds, inputNeighbourTypes, inputNeighbourEdgeIds);
        int expectedSize = inputNeighbourIds.length;
        Assert.assertEquals(expectedSize, adjacencyList.getSize());
        Assert.assertTrue(expectedSize <= adjacencyList.neighbourIds.length); // Check capacity.
        Assert.assertArrayEquals(sortedNeighbourIds,
                Arrays.copyOf(adjacencyList.neighbourIds, adjacencyList.getSize()));
        Assert.assertArrayEquals(sortedNeighbourTypes, Arrays.copyOf(adjacencyList.edgeTypes, adjacencyList.getSize()));
    }

    private void testSearch(int[] inputNeighbourIds, short[] inputNeighbourTypes, long[] inputNeighbourEdgeIds,
                            int neighbourIdForSearch, short edgeTypeForSearch, int expectedIndex) {
        SortedAdjacencyList adjacencyList =
                getPopulatedAdjacencyList(inputNeighbourIds, inputNeighbourTypes, inputNeighbourEdgeIds);
        int resultIndex = adjacencyList.search(neighbourIdForSearch, edgeTypeForSearch);
        Assert.assertEquals(expectedIndex, resultIndex);
    }

    @Test
    public void testCreationAndSortWithTypes() {
        int[] neighbourIds = {1, 32, 54, 34, 34, 12, 89, 0};
        short[] neighbourTypes = {4, 3, 3, 1, 9, 0, 10, 5};
        long[] neighbourEdgeIds = {0, 1, 2, 3, 4, 5, 6, 7};
        int[] sortedNeighboursIds = {0, 1, 12, 32, 34, 34, 54, 89};
        short[] sortedNeighbourTypes = {5, 4, 0, 3, 1, 9, 3, 10};
        long[] sortedNeighbourEdgeIds = {7, 0, 5, 1, 3, 4, 2, 6};
        testSort(neighbourIds, neighbourTypes, neighbourEdgeIds, sortedNeighboursIds, sortedNeighbourTypes,
                sortedNeighbourEdgeIds);
    }

    @Test
    public void testSortWithMultipleTypesForSingleEdge() {
        int[] neighbourIds = {1, 32, 54, 34, 34, 34, 12, 89, 0, 14, 7};
        short[] neighbourTypes = {4, 3, 3, 1, 9, 4, 0, 10, 5, 3, 0};
        long[] neighbourEdgeIds = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int[] sortedNeighboursIds = {0, 1, 7, 12, 14, 32, 34, 34, 34, 54, 89};
        short[] sortedNeighbourTypes = {5, 4, 0, 0, 3, 3, 1, 4, 9, 3, 10};
        long[] sortedNeighbourEdgeIds = {8, 0, 10, 6, 9, 1, 3, 5, 4, 2, 7};
        testSort(neighbourIds, neighbourTypes, neighbourEdgeIds, sortedNeighboursIds, sortedNeighbourTypes,
                sortedNeighbourEdgeIds);
    }

    @Test
    public void testSearchWithMultipleTypesForSingleEdge() {
        int[] neighbourIds = {1, 32, 54, 34, 34, 34, 12, 89, 0, 14, 7};
        short[] neighbourTypes = {4, 3, 3, 1, 9, 4, 0, 10, 5, 3, 0};
        long[] neighbourEdgeIds = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int neighbourIdForSearch = 34;
        short edgeTypeForSearch = 4;
        int expectedIndex = 7;
        testSearch(neighbourIds, neighbourTypes, neighbourEdgeIds, neighbourIdForSearch, edgeTypeForSearch,
                expectedIndex);
    }

    @Test
    public void testSearchWithFirstValueOfMultipleTypes() {
        int[] neighbourIds = {1, 32, 54, 34, 34, 34, 12, 89, 0, 14};
        short[] neighbourTypes = {4, 3, 3, 1, 9, 4, 0, 10, 5, 3};
        long[] neighbourEdgeIds = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int neighbourIdForSearch = 34;
        short edgeTypeForSearch = 1;
        int expectedIndex = 5;
        testSearch(neighbourIds, neighbourTypes, neighbourEdgeIds, neighbourIdForSearch, edgeTypeForSearch,
                expectedIndex);
    }

    @Test
    public void testSearchMatchIndexNotPowerOf2() {
        int[] neighbourIds = {1, 32, 54, 34, 34, 34, 12, 89, 0, 14, 7};
        short[] neighbourTypes = {4, 3, 3, 1, 9, 4, 0, 10, 5, 3, 0};
        long[] neighbourEdgeIds = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int neighbourIdForSearch = 32;
        short edgeTypeForSearch = 3;
        int expectedIndex = 5;
        testSearch(neighbourIds, neighbourTypes, neighbourEdgeIds, neighbourIdForSearch, edgeTypeForSearch,
                expectedIndex);
    }

    @Test
    public void testSearchForNeighbourIdLargerThanInList() {
        int[] neighbourIds = {1, 32, 54, 34, 34, 34, 12, 89, 0, 14, 7};
        short[] neighbourTypes = {4, 3, 3, 1, 9, 4, 0, 10, 5, 3, 0};
        long[] neighbourEdgeIds = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int neighbourIdForSearch = 100;
        short edgeTypeForSearch = 4;
        int expectedIndex = -10;
        testSearch(neighbourIds, neighbourTypes, neighbourEdgeIds, neighbourIdForSearch, edgeTypeForSearch,
                expectedIndex);
    }

    @Test
    public void testSearchWithSingleTypeForSingleEdge() {
        int[] neighbourIds = {1, 32, 54, 34, 34, 34, 12, 89, 0, 14, 7};
        short[] neighbourTypes = {4, 3, 3, 1, 9, 4, 0, 10, 5, 3, 0};
        long[] neighbourEdgeIds = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int neighbourIdForSearch = 7;
        short edgeTypeForSearch = TypeAndPropertyKeyStore.ANY;
        int expectedIndex = 2;
        testSearch(neighbourIds, neighbourTypes, neighbourEdgeIds, neighbourIdForSearch, edgeTypeForSearch,
                expectedIndex);
    }

    @Test
    public void testSearchWithNonExistentNeighbourType() {
        int[] neighbourIds = {1, 32, 54, 34, 34, 34, 12, 89, 0, 14, 7};
        short[] neighbourTypes = {4, 3, 3, 1, 9, 4, 0, 10, 5, 3, 0};
        long[] neighbourEdgeIds = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int neighbourIdForSearch = 7;
        short edgeTypeForSearch = 10;
        int expectedIndex = -2;
        testSearch(neighbourIds, neighbourTypes, neighbourEdgeIds, neighbourIdForSearch, edgeTypeForSearch,
                expectedIndex);
    }

    @Test
    public void testSearchWithNonExistentNeighbour() {
        int[] neighbourIds = {1, 32, 54, 34, 34, 34, 12, 89, 0, 14, 7};
        short[] neighbourTypes = {4, 3, 3, 1, 9, 4, 0, 10, 5, 3, 0};
        long[] neighbourEdgeIds = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int neighbourIdForSearch = 70;
        short edgeTypeForSearch = 10;
        int expectedIndex = -9;
        testSearch(neighbourIds, neighbourTypes, neighbourEdgeIds, neighbourIdForSearch, edgeTypeForSearch,
                expectedIndex);
    }

    @Test
    public void testSearchWithSmallSizeArrays() {
        int[] neighbourIds = {1, 3};
        short[] neighbourTypes = {1, 3};
        long[] neighbourEdgeIds = {0, 1};
        int neighbourIdForSearch = 1;
        short edgeTypeForSearch = 1;
        int expectedIndex = 0;
        testSearch(neighbourIds, neighbourTypes, neighbourEdgeIds, neighbourIdForSearch, edgeTypeForSearch,
                expectedIndex);
    }

    @Test
    public void testSearchReturnIndex0() {
        int[] neighbourIds = {2, 4, 7, 12, 14, 32, 34, 34, 34, 54, 89};
        short[] neighbourTypes = {5, 4, 0, 0, 3, 3, 1, 4, 9, 3, 10};
        long[] neighbourEdgeIds = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int neighbourIdForSearch = 2;
        short edgeTypeForSearch = 5;
        int expectedIndex = 0;
        testSearch(neighbourIds, neighbourTypes, neighbourEdgeIds, neighbourIdForSearch, edgeTypeForSearch,
                expectedIndex);
    }

    @Test
    public void testSearchReturnIntMinValueIfNoMatchFoundAndIndex0() {
        int[] neighbourIds = {2, 4, 7, 12, 14, 32, 34, 34, 34, 54, 89};
        short[] neighbourTypes = {5, 4, 0, 0, 3, 3, 1, 4, 9, 3, 10};
        long[] neighbourEdgeIds = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int neighbourIdForSearch = 1;
        short edgeTypeForSearch = 3;
        int expectedIndex = Integer.MIN_VALUE;
        testSearch(neighbourIds, neighbourTypes, neighbourEdgeIds, neighbourIdForSearch, edgeTypeForSearch,
                expectedIndex);
    }

    @Test
    public void testSearchReturnIntMinValueIfEdgeTypeDoesNotMatchAndIndex0() {
        int[] neighbourIds = {2, 4, 7, 12, 14, 32, 34, 34, 34, 54, 89};
        short[] neighbourTypes = {5, 4, 0, 0, 3, 3, 1, 4, 9, 3, 10};
        long[] neighbourEdgeIds = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int neighbourIdForSearch = 2;
        short edgeTypeForSearch = 4;
        int expectedIndex = Integer.MIN_VALUE;
        testSearch(neighbourIds, neighbourTypes, neighbourEdgeIds, neighbourIdForSearch, edgeTypeForSearch,
                expectedIndex);
    }

    @Test
    public void testRemoveNeighbourWithShortNeighbourAndTypeArrays() {
        int[] neighbourIds = {1, 3};
        short[] neighbourTypes = {1, 3};
        long[] neighbourEdgeIds = {0, 1};
        SortedAdjacencyList adjacencyList = getPopulatedAdjacencyList(neighbourIds, neighbourTypes, neighbourEdgeIds);
        int neighbourIdForRemove = 1;
        short edgeTypeForRemove = 1;
        adjacencyList.removeNeighbour(neighbourIdForRemove, edgeTypeForRemove);
        int expectedIndex = Integer.MIN_VALUE;
        Assert.assertEquals(expectedIndex, adjacencyList.search(neighbourIdForRemove, edgeTypeForRemove));
        Assert.assertEquals(1, adjacencyList.getSize());
        int[] expectedNeighbours = {3};
        Assert.assertArrayEquals(expectedNeighbours,
                Arrays.copyOf(adjacencyList.neighbourIds, adjacencyList.getSize()));
    }

    @Test
    public void testIntersectionWithEdgeType() {
        int[] neighbourIds1 = {1, 32, 54, 34, 34, 34, 12, 89, 0, 14, 7};
        short[] neighbourTypes1 = {4, 3, 3, 1, 9, 3, 0, 10, 5, 3, 0};
        long[] neighbourEdgeIds1 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        SortedAdjacencyList adjacencyList1 =
                getPopulatedAdjacencyList(neighbourIds1, neighbourTypes1, neighbourEdgeIds1);
        int[] neighbourIds2 = {1, 9, 14, 23, 34, 54, 89};
        short[] neighbourTypes2 = {4, 14, 3, 13, 3, 3, 23};
        long[] neighbourEdgeIds2 = {0, 1, 2, 3, 4, 5, 6};
        SortedAdjacencyList adjacencyList2 =
                getPopulatedAdjacencyList(neighbourIds2, neighbourTypes2, neighbourEdgeIds2);
        short intersectionFilterEdgeType = 3;
        IntArrayList listToIntersect = new IntArrayList();
        listToIntersect.addAll(Arrays.copyOf(adjacencyList2.neighbourIds, adjacencyList2.getSize()));
        IntArrayList intersections = adjacencyList1.getIntersection(listToIntersect, intersectionFilterEdgeType);
        int[] expectedNeighbours = {14, 34, 54};
        Assert.assertArrayEquals(expectedNeighbours, intersections.toArray());
    }

    @Test
    public void testIntersectionWithNoEdgeType() {
        int[] neighbourIds1 = {1, 32, 54, 34, 34, 34, 12, 89, 0, 14, 7};
        short[] neighbourTypes1 = {4, 3, 3, 1, 9, 3, 0, 10, 5, 3, 0};
        long[] neighbourEdgeIds1 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        SortedAdjacencyList adjacencyList1 =
                getPopulatedAdjacencyList(neighbourIds1, neighbourTypes1, neighbourEdgeIds1);
        int[] neighbourIds2 = {1, 9, 14, 23, 34, 54, 89};
        short[] neighbourTypes2 = {4, 14, 3, 13, 3, 3, 23};
        long[] neighbourEdgeIds2 = {0, 1, 2, 3, 4, 5, 6};
        SortedAdjacencyList adjacencyList2 =
                getPopulatedAdjacencyList(neighbourIds2, neighbourTypes2, neighbourEdgeIds2);
        IntArrayList listToIntersect = new IntArrayList();
        listToIntersect.addAll(Arrays.copyOf(adjacencyList2.neighbourIds, adjacencyList2.getSize()));
        IntArrayList intersections = adjacencyList1.getIntersection(listToIntersect, TypeAndPropertyKeyStore.ANY);
        int[] expectedNeighbours = {1, 14, 34, 54, 89};
        Assert.assertArrayEquals(expectedNeighbours, intersections.toArray());
    }

    @Test
    public void testIntersectionWithIntegerMinReturns() {
        int[] neighbourIds1 = {2, 4, 7, 12, 14, 32, 34, 34, 34, 54, 89};
        short[] neighbourTypes1 = {5, 4, 0, 0, 3, 3, 1, 4, 9, 3, 10};
        long[] neighbourEdgeIds1 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        SortedAdjacencyList adjacencyList1 =
                getPopulatedAdjacencyList(neighbourIds1, neighbourTypes1, neighbourEdgeIds1);
        int[] neighbourIds2 = {1, 9, 14, 23, 34, 54, 89};
        short[] neighbourTypes2 = {4, 14, 3, 13, 3, 3, 23};
        long[] neighbourEdgeIds2 = {0, 1, 2, 3, 4, 5, 6};
        SortedAdjacencyList adjacencyList2 =
                getPopulatedAdjacencyList(neighbourIds2, neighbourTypes2, neighbourEdgeIds2);
        IntArrayList listToIntersect = new IntArrayList();
        listToIntersect.addAll(Arrays.copyOf(adjacencyList2.neighbourIds, adjacencyList2.getSize()));
        IntArrayList intersections = adjacencyList1.getIntersection(listToIntersect, TypeAndPropertyKeyStore.ANY);
        int[] expectedNeighbours = {14, 34, 54, 89};
        Assert.assertArrayEquals(expectedNeighbours, intersections.toArray());
    }

    @Test
    public void testGetFilteredNeighbourIds() {
        int[] neighbourIds = {1, 9, 14, 23, 34, 54, 89};
        short[] edgeTypes = {1, 1, 2, 3, 2, 1, 1};
        long[] edgeIds = {0, 1, 2, 3, 4, 5, 6};

        ShortArrayList vertexTypes = new ShortArrayList();
        for (int i = 0; i < neighbourIds.length; ++i) {
            if (neighbourIds[i] % 2 == 0) {
                vertexTypes.set(neighbourIds[i], (short) 0);
            } else {
                vertexTypes.set(neighbourIds[i], (short) 1);
            }
        }

        SortedAdjacencyList adjacencyList = getPopulatedAdjacencyList(neighbourIds, edgeTypes, edgeIds);

        IntArrayList filteredNeighbourIds = adjacencyList
                .getFilteredNeighbourIds(TypeAndPropertyKeyStore.ANY, TypeAndPropertyKeyStore.ANY,
                        null /* no vertexTypes*/);
        Assert.assertArrayEquals(neighbourIds, filteredNeighbourIds.toArray());

        filteredNeighbourIds =
                adjacencyList.getFilteredNeighbourIds(TypeAndPropertyKeyStore.ANY, (short) 1, null /* no vertexTypes*/);
        Assert.assertArrayEquals(new int[]{1, 9, 54, 89}, filteredNeighbourIds.toArray());

        filteredNeighbourIds =
                adjacencyList.getFilteredNeighbourIds((short) 0, TypeAndPropertyKeyStore.ANY, vertexTypes);
        Assert.assertArrayEquals(new int[]{14, 34, 54}, filteredNeighbourIds.toArray());

        filteredNeighbourIds =
                adjacencyList.getFilteredNeighbourIds((short) 3, TypeAndPropertyKeyStore.ANY, vertexTypes);
        Assert.assertArrayEquals(new int[]{}, filteredNeighbourIds.toArray());

        filteredNeighbourIds = adjacencyList.getFilteredNeighbourIds((short) 0, (short) 1, vertexTypes);
        Assert.assertArrayEquals(new int[]{54}, filteredNeighbourIds.toArray());
    }
}
