package graph;

import lombok.Data;
import simulation.SimulationEntity;

import java.util.ArrayList;
import java.util.List;

@Data
public class Graph<N extends Node,E extends Edge> implements SimulationEntity {

    private List<N> nodes = new ArrayList<N>();
    private List<E> edges = new ArrayList<E>();
    private List<Path> connections = new ArrayList<Path>();

    public Graph(List<N> nodes, List<E> edges){
        this.nodes = nodes;
        this.edges = edges;
    }

    public void tick(){
        //First handle load fluctuations and migrations
        for(N node : nodes){
            node.tick();
        }
        //TODO: is this necessary?
        for(E edge : edges){
            edge.tick();
        }
        //Then update the connections that are changed
        for(Path connection : connections){
            connection.tick();
        }
        /**
         * TODO: implement
         */
    }

    /**
     * Gets the shortest path of edges from node1 to node2
     * @param node1
     * @param node2
     */
    public Path getPath(Node node1, Node node2){
        // TODO
        for(Edge edge : node1.getEdges()){
            if(edge.getFirstNode().equals(node2) || edge.getSecondNode().equals(node2)){
                //return new Path(new ArrayList<E>)
            }
        }
        return null;
    }
}
