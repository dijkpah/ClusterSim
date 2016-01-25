package graph;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Graph<N extends Node, E extends Edge> {

    protected List<N> nodes = new ArrayList<N>();
    protected List<E> edges = new ArrayList<E>();

    public Graph(List<N> nodes, List<E> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    /**
     * Find the edge between the given nodes, or null if it does not exist.
     * @param node1
     * @param node2
     * @return
     */
    public E findEdge(N node1, N node2) {
        for (E edge : edges) {
            if ((edge.getFirstNode().equals(node1) && edge.getSecondNode().equals(node2)) ||
                    (edge.getFirstNode().equals(node2) && edge.getSecondNode().equals(node1))
                    ) {
                return edge;
            }
        }
        return null;
    }
}
