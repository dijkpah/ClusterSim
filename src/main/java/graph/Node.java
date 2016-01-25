package graph;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import simulation.SimulationEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public Set<Node> getNeighbours(){
        Set<Node> result = new HashSet<>();
        for(Edge edge : edges){
            Node firstNode = edge.getFirstNode();
            if(firstNode.equals(this)){
                result.add(edge.getSecondNode());
            }else{
                result.add(firstNode);
            }
        }
        return result;
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
