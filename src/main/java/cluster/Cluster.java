package cluster;

import graph.Edge;
import graph.Graph;
import graph.Node;
import graph.Path;
import lombok.Data;
import simulation.ExcelLogger;
import simulation.SimulationEntity;
import switches.Switch;

import java.util.ArrayList;
import java.util.List;

@Data
public class Cluster<N extends Node, E extends Edge> extends Graph<N, E> implements SimulationEntity {

    private ExcelLogger excelLogger = new ExcelLogger();
    private List<Connection> connections = new ArrayList<Connection>();

    public Cluster(List<N> nodes, List<E> edges) {
        super(nodes, edges);
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
            // Update connections
        }

        /*for (N node : nodes) {

        }*/




        //TODO: is this necessary?
        for (E edge : edges) {
            edge.tick();
        }
        //Then update the connections that are changed
        for (Path connection : connections) {
            connection.tick();
        }
        /**
         * TODO: implement
         */
        updateLog();
    }
}
