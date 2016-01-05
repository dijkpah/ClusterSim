package simulation;

import graph.Edge;
import graph.Graph;
import graph.Node;

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

    /**
     * Start the simulation
     */
    public void start() {

    }

    public static void main(String[] args) {
        // Build the graph
        List<Node> nodes = new ArrayList<Node>();
        List<Edge> edges = new ArrayList<Edge>();
        Graph<Node, Edge> graph = new Graph<Node, Edge>(nodes, edges);

        // Create the simulation
        ClusterSimulation simulation = new ClusterSimulation(graph);

        // Start
        simulation.start();
    }
}
