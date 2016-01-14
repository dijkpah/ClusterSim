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
     * The cluster this simulation runs on.
     */
    @NonNull private Cluster<Node, Cable> cluster;

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
            // Update load and network traffic
            cluster.tick();
            // Determine migrations
            List<Migration> migrations = migrationPolicy.update(cluster);
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
        //Path path = cluster.getPath(migration);
    }

    public static void main(String[] args) {
        // Build the cluster
        Cluster<Node, Cable> cluster = simpleCluster();

        System.out.println(cluster);

        // Create the simulation
        ClusterSimulation simulation = new ClusterSimulation(cluster, new NoMigrationPolicy());

        //Set time
        int ticks = 15;

        // Start
        simulation.run(ticks);
    }

    /**
     * Creates a simple cluster with one switch, two servers and one VM on both server.
     */
    public static Cluster<Node, Cable> simpleCluster() {
        List<Node> nodes = new ArrayList<Node>();
        List<Cable> edges = new ArrayList<Cable>();

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

        // Vms are linked
        vm1.connectToVM(vm2);
        vm2.connectToVM(vm1);

        // Return the cluster
        return new Cluster<Node, Cable>(world, nodes, edges);
    }

    /**
     * Create a new cable between the two nodes.
     * @param node1 The first node.
     * @param node2 The second node.
     * @return A Cable between the nodes.
     */
    private static Cable createCable(Node node1, Node node2) {
        Cable cable = new Cable(node1, node2, 1000);
        node1.addEdge(cable);
        node2.addEdge(cable);
        return cable;
    }
}
