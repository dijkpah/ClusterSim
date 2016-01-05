package graph;

import java.util.ArrayList;
import java.util.List;

public abstract class Node {
    List<Edge> edges;

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

    public List<Edge> getEdges(){
        return this.edges;
    }
}
