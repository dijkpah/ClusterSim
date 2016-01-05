package graph;

import lombok.Data;
import simulation.SimulationEntity;

import java.util.ArrayList;
import java.util.List;

@Data
public abstract class Node implements SimulationEntity {

    private List<Edge> edges = new ArrayList<Edge>();

    public Node(List<Edge> edges){
        this.edges = new ArrayList<Edge>();
        this.addEdges(edges);
    }
    public Node(){
        this.edges = new ArrayList<Edge>();
    }

    public void addEdge(Edge edge){
        this.edges.add(edge);
    }

    public void addEdges(List<Edge> edges){
        this.edges.addAll(edges);
    }

    public abstract void tick();
}
