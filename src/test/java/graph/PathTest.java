package graph;

import cluster.Cable;
import cluster.Cluster;
import cluster.ClusterFactory;
import cluster.World;
import org.junit.Before;
import org.junit.Test;
import switches.HubSwitch;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class PathTest {


    List<Node> nodes;
    List<Edge> edges;

    @Before
    public void init(){
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
    }

    @Test
    public void testFindShortestPathSimple() {

        Node firstNode = new HubSwitch(0);
        Node lastNode = new HubSwitch(1);
        nodes.add(firstNode);
        nodes.add(lastNode);

        Cable cable1 = ClusterFactory.createCable(firstNode, lastNode, 10000);
        edges.add(cable1);

        Cluster graph = new Cluster("Cluster", new World(-1), nodes, edges);

        Path path = new Path(graph, firstNode, lastNode);

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

        nodes.add(firstNode);
        nodes.add(lastNode);
        nodes.add(node10);
        nodes.add(node11);
        nodes.add(node12);

        // shortest path
        edges.add(ClusterFactory.createCable(firstNode, node10, 10000));
        edges.add(ClusterFactory.createCable(lastNode, node10, 10000));

        // Additional path
        edges.add(ClusterFactory.createCable(node10, node11, 10000));
        edges.add(ClusterFactory.createCable(node11, lastNode, 10000));

        edges.add(ClusterFactory.createCable(firstNode, node12, 10000));

        Cluster graph = new Cluster("Cluster", new World(-1), nodes, edges);

        Path path = new Path(graph, firstNode, lastNode);

        assertTrue(path.getFirstEndPoint().equals(firstNode) ^ path.getSecondEndPoint().equals(firstNode));
        assertTrue(path.getFirstEndPoint().equals(lastNode) ^ path.getSecondEndPoint().equals(lastNode));
        assertNotEquals(path.getFirstEndPoint(), path.getSecondEndPoint());
        assertEquals(2, path.getEdges().size());
    }
}
