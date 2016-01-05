package graph;

import java.util.List;

public class Graph<N,E> {

    List<N> nodes;
    List<E> edges;

    public Graph(List<N> nodes, List<E> edges){
        this.nodes = nodes;
        this.edges = edges;
    }

}
