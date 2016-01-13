package cluster;

import cluster.Cable;
import graph.Edge;
import graph.Node;
import graph.Path;
import lombok.NonNull;

import java.util.List;

public class Connection extends Path {

    @NonNull private List<Cable> edges;
    @NonNull private Node firstEndPoint;
    @NonNull private Node secondEndPoint;


    public Connection(List<Edge> edges, Node firstEndPoint, Node secondEndPoint) {
        super(edges, firstEndPoint, secondEndPoint);
    }
}
