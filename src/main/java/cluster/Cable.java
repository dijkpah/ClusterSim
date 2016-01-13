package cluster;

import graph.Edge;
import graph.Node;
import lombok.Data;

@Data
public class Cable extends Edge {

    public Cable(Node node1, Node node2) {
        super(node1, node2);
    }

    @Override
    public void tick() {
        //TODO
    }
}
