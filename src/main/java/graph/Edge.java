package graph;

public abstract class Edge {

    Node first;
    Node second;

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
