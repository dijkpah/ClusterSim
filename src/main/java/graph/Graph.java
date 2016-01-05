package graph;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Graph<N,E> {

    private List<N> nodes = new ArrayList<N>();
    private List<E> edges = new ArrayList<E>();

    public Graph(List<N> nodes, List<E> edges){
        this.nodes = nodes;
        this.edges = edges;
    }

}
