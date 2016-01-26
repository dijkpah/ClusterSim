package graph;

import cluster.Cluster;
import lombok.Data;
import lombok.NonNull;
import simulation.SimulationEntity;

import java.util.*;

@Data
public class Path implements SimulationEntity {
    @NonNull protected List<Edge> edges;
    @NonNull protected Node firstEndPoint;
    @NonNull protected Node secondEndPoint;

    public void tick() {
    }

    public void reset() {

    }

    public Path(Cluster graph, Node firstEndPoint, Node secondEndPoint) {
        this.firstEndPoint = firstEndPoint;
        this.secondEndPoint = secondEndPoint;

        this.updateEdges(graph);
    }

    public void updateEdges(Cluster graph){

        this.edges = findShortestPath(graph, firstEndPoint, secondEndPoint);
        if(this.edges == null){
            throw new RuntimeException("A Path is specified, but the path could not be found");
        }
    }

    /**
     * Return the edges in the shortest path between the two endpoints, or null if such a path does not exist.
     *
     * @param firstEndPoint  The start node
     * @param secondEndPoint The end node
     * @return The edges in the shortest path between the two endpoints, or null if such a path does not exist.
     */

    private List<Edge> findShortestPath(Cluster graph, Node firstEndPoint, Node secondEndPoint) {
       return dijkstra(graph, firstEndPoint, secondEndPoint);
    }

    private List<Edge> dijkstra(Cluster graph, Node source, Node target){
        List<Node> q = new ArrayList<>();
        HashMap<Node, Integer> dist = new HashMap<>();
        HashMap<Node, Node> prev = new HashMap<>();
        List<Edge> shortest = new ArrayList<>();
        Node u = source;

        for(Object o : graph.getNodes()) {
            Node node = ((Node) o);
            q.add(node);
            dist.put(node, Integer.MAX_VALUE);
        }

        dist.put(source, 0);

        while(!q.isEmpty() && !u.equals(target)){
            u = this.getSmallestDistance(dist,q).getKey();
            q.remove(u);

            for(Node v : u.getNeighbours()){
                int alt = dist.get(u) + 1;//all connections have length 1
                if(alt<dist.get(v)){
                    dist.put(v, alt);
                    prev.put(v, u);
                }
            }
        }

        u = target;
        Node previous = prev.get(u);
        while(previous != null){

            boolean match = false;
            for(Edge edge : u.getEdges()){
                if(!match &&
                        (  (edge.getFirstNode().equals(u) && edge.getSecondNode().equals(previous))
                        || (edge.getFirstNode().equals(previous) && edge.getSecondNode().equals(u))
                        )
                    ){
                    shortest.add(edge);
                    match = true;
                }
            }
            u = previous;
            previous = prev.get(u);
        }
        return shortest;
    }

    private Map.Entry<Node, Integer> getSmallestDistance(HashMap<Node, Integer> dist, List<Node> q){
        Map.Entry<Node, Integer> minEntry = null;
        for(Node node : q){
            Integer value  = dist.get(node);
            if (minEntry == null || value.compareTo(minEntry.getValue()) < 0)
            {
                minEntry = new AbstractMap.SimpleEntry<>(node, value);
            }
        }
        return minEntry;
    }
}
