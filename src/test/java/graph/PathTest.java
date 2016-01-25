package graph;

import cluster.*;
import org.junit.Before;
import org.junit.Test;
import switches.HubSwitch;
import switches.Switch;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class PathTest {

    @Test
    public void testFindShortestPathSimple() {
        Node firstNode = new HubSwitch(0);
        Node lastNode = new HubSwitch(1);

        Cable cable1 = ClusterFactory.createCable(firstNode, lastNode);

        Path path = new Path(firstNode, lastNode);

        assertEquals(1, path.getEdges().size());
        assertEquals(cable1, path.getEdges().get(0));
    }

    @Test
    public void testFindShortestPathDifficult() {
        Node firstNode = new HubSwitch(0);
        Node lastNode = new HubSwitch(1);

        Node node10 = new HubSwitch(10);
        Node node11 = new HubSwitch(11);
        Node node12 = new HubSwitch(12);

        // shortest path
        ClusterFactory.createCable(firstNode, node10);
        ClusterFactory.createCable(lastNode, node10);

        // Additional path
        ClusterFactory.createCable(node10, node11);
        ClusterFactory.createCable(node11, lastNode);

        ClusterFactory.createCable(firstNode, node12);

        Path path = new Path(firstNode, lastNode);

        assertTrue(path.getFirstEndPoint().equals(firstNode) ^ path.getSecondEndPoint().equals(firstNode));
        assertTrue(path.getFirstEndPoint().equals(lastNode) ^ path.getSecondEndPoint().equals(lastNode));
        assertNotEquals(path.getFirstEndPoint(), path.getSecondEndPoint());
        assertEquals(2, path.getEdges().size());
    }
}
