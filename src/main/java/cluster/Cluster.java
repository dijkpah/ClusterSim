package cluster;

import graph.Graph;
import graph.Node;
import graph.Path;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import simulation.ExcelLogger;
import simulation.SimulationEntity;
import switches.Switch;
import vm.VM;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Cluster<N extends Node, E extends Cable> extends Graph<N, E> implements SimulationEntity {
    private World world;

    private ExcelLogger excelLogger = new ExcelLogger();
    private List<Connection> connections = new ArrayList<Connection>();

    public Cluster(World world, List<N> nodes, List<E> edges) {
        super(nodes, edges);
        this.world = world;
    }

    public Connection getConnection(Connection.Type type, Node node1, Node node2) {
        Connection result = null;
        for (Connection connection : connections) {
            if ((connection.getFirstEndPoint().equals(node1) && connection.getSecondEndPoint().equals(node2))
            ||  (connection.getFirstEndPoint().equals(node2) && connection.getSecondEndPoint().equals(node1))) {
                result = connection;
            }
        }
        if (result == null) {
            result = new Connection(type, node1, node2);
            connections.add(result);
        }
        return result;
    }

    /**
     * Enters power consumption summations for servers and connections
     */
    private void updateLog(){
        int serverConsumption = 0;
        int baseSwitchConsumption = 0;
        int externalNetworkConsumption = 0;
        int internalNetworkConsumption = 0;
        int migrationNetworkConsumption= 0;

        for(Node node : nodes){
            if(node instanceof Server){
                serverConsumption += ((Server) node).getPowerUsage();
            }else if(node instanceof Switch){
                Switch aSwitch = (Switch) node;
                baseSwitchConsumption += aSwitch.getBaseConsumption();
                externalNetworkConsumption += aSwitch.getExternalCommunicationConsumption();
                internalNetworkConsumption += aSwitch.getInternalCommunicationConsumption();
                migrationNetworkConsumption+= aSwitch.getMigrationCommunicationConsumption();
            }else{
                new Exception("unknown Node type: "+node.getClass().getName()).printStackTrace();
            }
        }
        excelLogger.addTick(serverConsumption, baseSwitchConsumption, externalNetworkConsumption, internalNetworkConsumption, migrationNetworkConsumption);
    }

    public void tick() {
        // Update load on the nodes
        for (N node : nodes) {
            // Update load and network traffic
            node.tick();

        }

        // Reset connection status
        for(Connection connection : connections){
            connection.setNetworkTraffic(0);
        }
        for(Cable cable : edges){
            cable.setExternalCommunicationBandwidth(0);
            cable.setInternalCommunicationBandwidth(0);
            cable.setMigrationBandwidth(0);
        }

        // Update connections
        for (N node : nodes) {
            Connection connection;
            if(node instanceof Server){
                Server server = (Server) node;
                for(VM vm : server.getVms()){
                    // Connection to the world
                    connection = this.getConnection(Connection.Type.EXTERNAL, server, this.getWorld());
                    if(connection==null){
                        System.err.println("WHAAAAAAAAAAA");
                    }
                    connection.addNetworkTraffic(vm.getNetworkTrafficToWorld());

                    // Connections between VMs
                    for (Map.Entry<VM, Integer> other : vm.getConnectedVMs().entrySet()){
                        connection = this.getConnection(Connection.Type.INTERNAL, server, other.getKey().getServer());
                        if(connection==null){
                            System.err.println("WHAAAAAAAAAAA");
                        }
                        connection.addNetworkTraffic(other.getValue());
                    }
                }
            }
        }

        for(Connection connection : connections){
            connection.applyNetworkTraffic();
        }

        /**
         * TODO: implement
         */
        updateLog();
    }
}
