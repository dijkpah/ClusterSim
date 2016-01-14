package graph;

import lombok.Data;
import lombok.NonNull;
import simulation.SimulationEntity;

@Data
public abstract class Edge implements SimulationEntity{

    @NonNull private Node firstNode;
    @NonNull private Node secondNode;
    @NonNull private int weight;

    public void setNodes(Node firstNode, Node secondNode, int weight){
        this.firstNode = firstNode;
        this.secondNode = secondNode;
        this.weight = weight;
    }

    public abstract void tick();

}
