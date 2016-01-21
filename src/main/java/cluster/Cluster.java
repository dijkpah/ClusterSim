package cluster;

import graph.Graph;
import graph.Node;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import simulation.SimulationEntity;
import vm.VM;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Cluster<N extends Node, E extends Cable> extends Graph<N, E> implements SimulationEntity {
    private World world;


    private List<Connection> connections = new ArrayList<Connection>();
    private List<Server> servers;

    public Cluster(World world, List<N> nodes, List<E> edges) {
        super(nodes, edges);
        this.world = world;
    }

    public Connection getConnection(Connection.Type type, Node node1, Node node2) {
        Connection result = null;
        for (Connection connection : connections) {
            if (connection.getType().equals(type) && (
                    (connection.getFirstEndPoint().equals(node1) && connection.getSecondEndPoint().equals(node2)) ||
                            (connection.getFirstEndPoint().equals(node2) && connection.getSecondEndPoint().equals(node1))
            )) {
                result = connection;
            }
        }
        if (result == null) {
            result = new Connection(type, node1, node2);
            connections.add(result);
        }
        return result;
    }

    @Override
    public void reset() {
        // Reset nodes
        for(N node : nodes){
            node.reset();
        }

        // Reset connection status
        for (Connection connection : connections) {
            connection.reset();
        }

        // Reset cable weights
        for (Cable cable : edges) {
            cable.reset();
        }
    }

    public void tick() {
        // Update load on the nodes
        for (N node : nodes) {
            // Update load and network traffic
            node.tick();
        }

        this.updateVMConnections();

        for (Connection connection : connections) {
            connection.tick();
        }
    }

    public void updateVMConnections() {
        // Update connections from VMs to the world and inter-VM
        for (N node : nodes) {
            Connection connection;
            if (node instanceof Server) {
                Server server = (Server) node;
                for (VM vm : server.getVms()) {
                    // Connection to the world
                    connection = this.getConnection(Connection.Type.EXTERNAL, server, this.getWorld());
                    if (connection == null) {
                        System.err.println("WHAAAAAAAAAAA");
                        return;
                    }
                    connection.addNetworkTraffic(vm.getNetworkTrafficToWorld());

                    // Connections between VMs
                    for (Map.Entry<VM, Integer> other : vm.getConnectedVMs().entrySet()) {
                        connection = this.getConnection(Connection.Type.INTERNAL, server, other.getKey().getServer());
                        if (connection == null) {
                            System.err.println("WHAAAAAAAAAAA");
                            return;
                        }
                        connection.addNetworkTraffic(other.getValue());
                    }
                }
            }
        }
    }

    /**
     * Get all servers which are currently available to host VMs.
     * In other words: servers which are not sleeping, booting up or shutting down.
     *
     * @return
     */
    public List<Server> getPossibleTargetServers() {
        return this.getServers().stream().filter(s -> s.getState().equals(Server.State.AVAILABLE)).collect(Collectors.toList());
    }

    private List<Server> getServers() {
        // Lazy loading
        if (servers == null) {
            servers = new ArrayList<Server>();
            for (Node node : nodes) {
                if (node instanceof Server) {
                    servers.add((Server) node);
                }
            }
        }
        return servers;
    }


}
