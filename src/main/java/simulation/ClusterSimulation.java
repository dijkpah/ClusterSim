package simulation;

import cluster.*;
import graph.Edge;
import graph.Graph;
import graph.Node;
import lombok.Data;
import lombok.NonNull;
import migration.Migration;
import migration.MigrationPolicy;
import migration.NoMigrationPolicy;
import vm.M4LargeVM;
import vm.VM;

import java.util.ArrayList;
import java.util.List;

/**
 * A simulation of a cluster
 */
@Data
public class ClusterSimulation {
    /**
     * The graph this simulation runs on.
     */
    @NonNull private Graph<Node, Edge> graph;

    /**
     * The MigrationPolicy to use in this simulation.
     */
    @NonNull private MigrationPolicy migrationPolicy;

    public long clock;

    /**
     * Start the simulation
     */
    public void run(int ticks) {
        clock = 0;
        while (clock < ticks) {
            graph.tick();
            List<Migration> migrations = migrationPolicy.update(graph);
            executeMigrations(migrations);
            clock++;
        }
    }

    private void executeMigrations(List<Migration> migrations) {
        for(Migration migration : migrations){
            executeMigration(migration);
        }

    }

    private void executeMigration(Migration migration) {
        //Path path = graph.getPath(migration);
    }

    public static void main(String[] args) {
        // Build the graph
        Graph<Node, Edge> graph = simpleCluster();

        System.out.println(graph);

        // Create the simulation
        ClusterSimulation simulation = new ClusterSimulation(graph, new NoMigrationPolicy());

        //Set time
        int ticks = 15;

        // Start
        simulation.run(ticks);
    }

    /**
     * Creates a simple cluster with one switch, two servers and one VM on both server.
     */
    public static Graph<Node, Edge> simpleCluster() {
        List<Node> nodes = new ArrayList<Node>();
        List<Edge> edges = new ArrayList<Edge>();

        // Create world
        World world = new World(0);

        Switch switch1 = new Switch(1);

        // Create servers
        Server server1 = new SmallServer(1);
        Server server2 = new SmallServer(2);

        // Create VMs
        VM vm1 = new M4LargeVM(1);
        VM vm2 = new M4LargeVM(2);

        // Add VMs to server
        server1.addVM(vm1);
        server2.addVM(vm2);

        // Add nodes
        nodes.add(world);
        nodes.add(switch1);
        nodes.add(server1);
        nodes.add(server2);

        // Create edges
        edges.add(createCable(server1, switch1));
        edges.add(createCable(server2, switch1));
        edges.add(createCable(switch1, world));

        // Setup connection
        new Connection(Connection.Type.EXTERNAL, server1, world);
        new Connection(Connection.Type.EXTERNAL, server2, world);
        new Connection(Connection.Type.INTERNAL, server1, server2);

        // Return the graph
        return new Graph<Node, Edge>(nodes, edges);
    }

    /**
     * Create a new cable between the two nodes.
     * @param node1 The first node.
     * @param node2 The second node.
     * @return A Cable between the nodes.
     */
    private static Cable createCable(Node node1, Node node2) {
        Cable cable = new Cable(node1, node2);
        node1.addEdge(cable);
        node2.addEdge(cable);
        return cable;
    }
}
