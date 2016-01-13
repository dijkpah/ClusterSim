package graph;

import lombok.Data;
import lombok.NonNull;
import simulation.SimulationEntity;

@Data
public abstract class Edge implements SimulationEntity{

    @NonNull private Node firstNode;
    @NonNull private Node secondNode;

    public void setNodes(Node firstNode, Node secondNode){
        this.firstNode = firstNode;
        this.secondNode = secondNode;
    }

    public abstract void tick();

}
