package cluster;

import graph.Node;
import graph.Path;
import lombok.NonNull;

import java.util.List;

public class Connection extends Path {

    @NonNull private List<Cable> edges;
    @NonNull private Node firstEndPoint;
    @NonNull private Node secondEndPoint;
    @NonNull private Type type;

    public Connection(Type type, Node firstEndPoint, Node secondEndPoint) {
        super(firstEndPoint, secondEndPoint);
        this.type = type;
    }

    public enum Type{
        MIGRATION,
        INTERNAL,
        EXTERNAL,
    }
}
