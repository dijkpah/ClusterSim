package graph;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import simulation.SimulationEntity;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString(exclude="edges")
@EqualsAndHashCode(exclude = "edges")
public abstract class Node implements SimulationEntity {

    @NonNull public final int id;

    private List<Edge> edges = new ArrayList<Edge>();

    public Node(int id, List<Edge> edges){
        this.id = id;
        this.edges = new ArrayList<Edge>();
        this.addEdges(edges);
    }
    public Node(int id){
        this.edges = new ArrayList<Edge>();
        this.id = id;
    }

    public void addEdge(Edge edge){
        this.edges.add(edge);
    }

    public void addEdges(List<Edge> edges){
        this.edges.addAll(edges);
    }

    public int getId(){
        return this.id;
    }

    public abstract void tick();
}
