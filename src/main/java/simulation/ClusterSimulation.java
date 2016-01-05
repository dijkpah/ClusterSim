package simulation;

import cluster.*;
import graph.Edge;
import graph.Graph;
import graph.Node;
import vm.M4LargeVM;
import vm.VM;

import java.util.ArrayList;
import java.util.List;

/**
 * A simulation of a cluster
 */
public class ClusterSimulation {
    /**
     * The graph this simulation runs on.
     */
    private Graph<Node, Edge> graph;

    /**
     * Creates a new simulation of a cluster with the given graph.
     *
     * @param graph The graph to run the simulation on.
     */
    public ClusterSimulation(Graph<Node, Edge> graph) {
        this.graph = graph;
    }

    public long clock = 0;

    /**
     * Start the simulation
     */
    public void run(int ticks) {
        int time = 0;
        while (time < ticks) {
            graph.tick();
            /**
             * TODO: implement
             */
        }
    }

    public static void main(String[] args) {
        // Build the graph
        Graph<Node, Edge> graph = simpleCluster();

        // Create the simulation
        ClusterSimulation simulation = new ClusterSimulation(graph);

        //Set time
        int ticks = 15;

        // Start
        simulation.run(ticks);
    }

    /**
     * Creates a simple cluster with one switch, two servers and one VM on both server.
     */
    public static Graph simpleCluster() {
        List<Node> nodes = new ArrayList<Node>();
        List<Edge> edges = new ArrayList<Edge>();

        // Create world
        World world = new World();

        Switch switch1 = new Switch();

        // Create servers
        Server server1 = new SmallServer();
        Server server2 = new SmallServer();

        // Create VMs
        VM vm1 = new M4LargeVM();
        VM vm2 = new M4LargeVM();

        // Add VMs to server
        server1.addVM(vm1);
        server2.addVM(vm2);

        // Add nodes
        nodes.add(world);
        nodes.add(switch1);
        nodes.add(server1);
        nodes.add(server2);

        // Create edges
        edges.add(createConnection(server1, switch1));
        edges.add(createConnection(server2, switch1));
        edges.add(createConnection(switch1, world));

        // Return the graph
        return new Graph<Node, Edge>(nodes, edges);
    }

    private static Connection createConnection(Node node1, Node node2) {
        Connection connection = new Connection(node1, node2);
        node1.addEdge(connection);
        node2.addEdge(connection);
        return connection;
    }
}
