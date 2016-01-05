package cluster;

import graph.Edge;
import graph.Node;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
public class Path {
    @NonNull private List<Edge> edges;
    @NonNull private Node firstEndPoint;
    @NonNull private Node secondEndPoint;
}
