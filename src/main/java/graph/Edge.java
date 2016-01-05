package graph;

import lombok.Data;
import lombok.NonNull;

@Data
public abstract class Edge {

    @NonNull private Node firstNode;
    @NonNull private Node secondNode;

    public void setNodes(Node firstNode, Node secondNode){
        this.firstNode = firstNode;
        this.secondNode = secondNode;
    }
}
