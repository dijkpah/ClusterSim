package graph;

import lombok.Data;

import java.util.List;

@Data
public class Graph<N,E> {

    private List<N> nodes;
    private List<E> edges;

    public Graph(List<N> nodes, List<E> edges){
        this.nodes = nodes;
        this.edges = edges;
    }

}
