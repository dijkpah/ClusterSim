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
}
