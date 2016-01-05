package cluster;

import graph.Edge;
import graph.Node;
import lombok.Data;

@Data
public class Connection extends Edge {

    public Connection(Node first, Node second){
        super(first, second);
    }

    @Override
    public void tick() {
        //TODO
    }
}
