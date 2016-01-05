package graph;

import java.util.List;

public class Node {
    List<Edge> edges;

    public Node(List<Edge> edges){
        this.edges = edges;
    }
    public Node(){

    }

    public void addEdge(Edge edge){
        this.edges.add(edge);
    }

    public List<Edge> getEdges(){
        return this.edges;
    }
}
