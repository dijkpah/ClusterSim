package simulation;

import cluster.*;
import graph.Edge;
import graph.Node;
import lombok.Data;
import lombok.NonNull;
import migration.Migration;
import migration.MigrationPolicy;
import migration.NoMigrationPolicy;
import switches.MainSwitch;
import switches.Switch;
import vm.M4LargeVM;
import vm.VM;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private Set<Migration> currentMigrations = new HashSet<>();

    /**
     * Start the simulation
     */
    public void run(int ticks) {
        clock = 0;
        while (clock < ticks) {
            // Update load and network traffic
            cluster.tick();
            // Determine migrations
            currentMigrations.addAll(migrationPolicy.update(cluster));
            // Apply migrations
            executeMigrations();
            clock++;
        }
    }

    private void executeMigrations() {
        for(Migration migration : currentMigrations){
            executeMigration(migration);
        }

        // Remove finished migrations
        currentMigrations.removeIf(m -> m.getTransferredData() >= m.getVm().getSize());
    }

    private void executeMigration(Migration migration) {
        // Update the state of the VMs
        migration.getVm().setState(VM.State.MIGRATING);
        migration.getTargetVM().setState(VM.State.RESERVED);

        // Get the connection
        Connection connection = cluster.getConnection(Connection.Type.MIGRATION, migration.getFrom(), migration.getTo());
        // Determine remaining bandwidth
        int bandwidth = connection.getBandwidth() - connection.getNetworkTraffic();
        // Use all remaining bandwidth
        connection.addNetworkTraffic(bandwidth);
        // Determine transferred bytes
        migration.setTransferredData(migration.getTransferredData() + Params.TICK_DURATION * bandwidth);

        // If all data is transfered, the migration is completed
        if(migration.getTransferredData() >= migration.getVm().getSize()){
            migration.getFrom().removeVM(migration.getVm());
            migration.getTo().removeVM(migration.getTargetVM());
            migration.getTo().addVM(migration.getVm());
            migration.getVm().setState(VM.State.RUNNING);
        }
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

        Switch switch1 = new MainSwitch(1);

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
        Cable cable = new Cable(node1, node2, Params.CABLE_CAPACITY);
        node1.addEdge(cable);
        node2.addEdge(cable);
        return cable;
    }
}
