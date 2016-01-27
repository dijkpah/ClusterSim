package cluster;

import graph.Node;
import simulation.Params;
import switches.MainSwitch;
import switches.Switch;
import vm.*;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class ClusterFactory {

    /**
     * Creates a simple cluster with one switch and the given number of servers.
     * No VMs are installed.
     * The world has id 0, the switch id 1 and the servers counting from 2.
     */
    public static Cluster<Node, Cable> simpleGroupedCluster() {
        List<Node> nodes = new ArrayList<>();
        List<Cable> edges = new ArrayList<>();

        // Create world
        World world = new World(0);
        nodes.add(world);

        // Create switch
        Switch switch1 = new MainSwitch(1);
        Switch switch2 = new MainSwitch(2);
        Switch switch3 = new MainSwitch(3);
        nodes.add(switch1);
        nodes.add(switch2);
        nodes.add(switch3);


        Server server1 = new Server(1);
        Server server2 = new Server(2);
        nodes.add(server1);
        nodes.add(server2);

        // connect to world
        edges.add(createCable(world, switch2, Params.CABLE_CAPACITY_MAIN_TO_WORLD));
        edges.add(createCable(switch1, switch2, Params.CABLE_CAPACITY_TOR_TO_HUB));
        edges.add(createCable(switch2, switch3, Params.CABLE_CAPACITY_TOR_TO_HUB));
        edges.add(createCable(server1, switch1, Params.CABLE_CAPACITY_SERVER_TO_TOR));
        edges.add(createCable(server2, switch3, Params.CABLE_CAPACITY_SERVER_TO_TOR));

        VM vm1 = new M42XLargeVM(1);
        VM vm2 = new M4XLargeVM(2);
        VMGroup group1 = new VMGroup(1, new TreeSet<>());
        group1.addVM(vm1);
        group1.addVM(vm2);


        VM vm3 = new M4XLargeVM(3);
        VM vm4 = new M4XLargeVM(4);
        VM vm5 = new M4XLargeVM(5);
        VMGroup group2 = new VMGroup(2, new TreeSet<>());
        group2.addVM(vm2);
        group2.addVM(vm3);
        group2.addVM(vm4);


        server1.addVM(vm1);
        server1.addVM(vm3);
        server2.addVM(vm2);
        server2.addVM(vm4);
        server2.addVM(vm5);

        // Return the cluster
        return new Cluster<>("Simple grouped cluster", world, nodes, edges);
    }

    /**
     * Creates a simple cluster with one switch and the given number of servers.
     * No VMs are installed.
     * The world has id 0, the switch id 1 and the servers counting from 2.
     */
    public static Cluster<Node, Cable> simpleEmptyCluster(int servers) {
        List<Node> nodes = new ArrayList<>();
        List<Cable> edges = new ArrayList<>();

        // Create world
        World world = new World(0);

        // Create switch
        Switch switch1 = new MainSwitch(1);

        // connect to world
        nodes.add(switch1);
        edges.add(createCable(world, switch1, Params.CABLE_CAPACITY_MAIN_TO_WORLD));

        // Create servers
        for(int j=0; j<servers; j++){
            Server server = new Server(j+2);
            nodes.add(server);
            edges.add(createCable(server, switch1, Params.CABLE_CAPACITY_SERVER_TO_TOR));
        }

        // Return the cluster
        return new Cluster<>("Simple empty cluster", world, nodes, edges);
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
        Server server1 = new Server(1);
        Server server2 = new Server(2);

        // Create VMs
        VM vm1 = new M4LargeVM(1);
        VM vm2 = new M4XLargeVM(2);
        VM vm3 = new M4XLargeVM(3);
        VM vm4 = new M4XLargeVM(4);
        VM vm5 = new M4XLargeVM(5);

        // Add VMs to server
        server2.addVM(vm1);
        server2.addVM(vm2);
        server2.addVM(vm3);
        server2.addVM(vm4);
        server2.addVM(vm5);

        // Add nodes
        nodes.add(world);
        nodes.add(switch1);
        nodes.add(server1);
        nodes.add(server2);

        // Create edges
        edges.add(createCable(server1, switch1, Params.CABLE_CAPACITY_SERVER_TO_TOR));
        edges.add(createCable(server2, switch1, Params.CABLE_CAPACITY_SERVER_TO_TOR));
        edges.add(createCable(switch1, world, Params.CABLE_CAPACITY_MAIN_TO_WORLD));

        // Vms are linked
        VMGroup group0 = new VMGroup(0, new TreeSet<>());
        group0.addVM(vm1);
        group0.addVM(vm2);

        VMGroup group1 = new VMGroup(0, new TreeSet<>());
        group1.addVM(vm3);
        group1.addVM(vm4);



        // Return the cluster
        return new Cluster<Node, Cable>("Simple cluster", world, nodes, edges);
    }

    /**
     * Create a new cable between the two nodes.
     *
     * @param node1 The first node.
     * @param node2 The second node.
     * @param capacity The capacity of the cable
     * @return A Cable between the nodes.
     */
    public static Cable createCable(Node node1, Node node2, int capacity) {
        Cable cable = new Cable(node1, node2, capacity);
        node1.addEdge(cable);
        node2.addEdge(cable);
        return cable;
    }
}
