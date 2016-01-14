package cluster;

import graph.Edge;
import graph.Node;
import lombok.Data;
import lombok.NonNull;

@Data
public class Cable extends Edge {

    private int externalCommunicationBandwidth = 0;
    private int internalCommunicationBandwidth = 0;
    private int migrationBandwidth = 0;
    @NonNull private int capacity;

    public Cable(Node node1, Node node2, int capacity) {
        super(node1, node2, 0);
        this.capacity = capacity;
    }

    @Override
    public void tick() {
        //TODO
    }

    @Override
    public int getWeight(){
        return externalCommunicationBandwidth + internalCommunicationBandwidth + migrationBandwidth;
    }

    public double getLoad(){
        return this.getWeight()/this.getCapacity();
    }

    public String toString(){
        return "\r\n<"+this.getFirstNode()+","+this.getSecondNode()+">";
    }
}
