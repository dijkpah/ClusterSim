package cluster;

import graph.Edge;
import graph.Node;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class Cable extends Edge {

    private int externalCommunicationBandwidth = 0;
    private int internalCommunicationBandwidth = 0;
    private int migrationBandwidth = 0;
    @NonNull private int capacity;

    public Cable(Node node1, Node node2, int capacity) {
        super(node1, node2, 0);//Weight is zero at start
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
        return "Cable(1="+this.getFirstNode().getId()+", 2="+this.getSecondNode().getId() + ", external=" + externalCommunicationBandwidth + ", internal=" + internalCommunicationBandwidth + ", migration=" + migrationBandwidth + ")";
    }
}
