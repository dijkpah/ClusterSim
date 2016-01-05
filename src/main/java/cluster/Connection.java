package cluster;

import graph.Edge;
import graph.Node;
import lombok.Data;

@Data
public class Connection extends Edge {

    public Connection(Node node1, Node node2) {
        super(node1, node2);
    }

    @Override
    public void tick() {
        //TODO
    }
}
