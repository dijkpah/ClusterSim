package graph;

import lombok.Data;

@Data
public abstract class Edge {

    private Node first;
    private Node second;

    public Edge(Node first, Node second){
        this.setEdges(first, second);
    }

    public Edge(){

    }

    public void setEdges(Node first, Node second){
        this.first = first;
        this.second = second;
    }
}
