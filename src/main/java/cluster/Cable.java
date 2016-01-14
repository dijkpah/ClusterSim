package cluster;

import graph.Edge;
import graph.Node;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Cable extends Edge {

    public Cable(Node node1, Node node2) {
        super(node1, node2, 0);
    }

    @Override
    public void tick() {
        //TODO
    }

    public String toString(){
        return "\r\n<"+this.getFirstNode()+","+this.getSecondNode()+">";
    }
}
