package graph;

import cluster.Path;
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
        for(N node : nodes){
            node.tick();
        }
        for(E edge : edges){
            edge.tick();
        }
        for(Path connection : connections){
            connection.tick();
        }
        /**
         * TODO: implement
         */
    }

}
