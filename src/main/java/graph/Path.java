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

    public Path(Node firstEndPoint, Node secondEndPoint) {
        this.firstEndPoint = firstEndPoint;
        this.secondEndPoint = secondEndPoint;

        this.updateEdges();
    }

    public void updateEdges(){

        this.edges = findShortestPath(firstEndPoint, secondEndPoint, new HashSet<>());
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
    private List<Edge> findShortestPath(Node firstEndPoint, Node secondEndPoint, Set<Edge> visited) {
        // Base case: first endpoint is the same as the second endpoint
        if (secondEndPoint.equals(firstEndPoint)) {
            return new ArrayList<>();
        } else {
            List<Edge> shortest = null;

            // Loop through all edges, and use the one which yields the shortest path
            for (Edge edge : firstEndPoint.getEdges()) {
                if(!visited.contains(edge)) {
                    List<Edge> result;
                    Set<Edge> newVisited = new HashSet<>(visited);
                    newVisited.add(edge);
                    if (edge.getFirstNode().equals(firstEndPoint)) {
                        result = findShortestPath(edge.getSecondNode(), secondEndPoint, newVisited);
                    } else {
                        result = findShortestPath(edge.getFirstNode(), secondEndPoint, newVisited);
                    }

                    // Compare the found path to the shortest
                    if (result != null && (shortest == null || result.size() < shortest.size() - 1)) {
                        result.add(edge);
                        shortest = result;
                    }
                }
            }
            return shortest;
        }
    }

    private List<Edge> dijkstra(Cluster graph, Node source, Node target){
        List<Node> q = new ArrayList<>();
        HashMap<Node, Integer> dist = new HashMap<>();
        List<Edge> shortest = new ArrayList<>();
        Node u = source;

        for(Object o : graph.getNodes()) {
            Node node = ((Node) o);
            q.add(node);
            dist.put(node, Integer.MAX_VALUE);
        }

        dist.put(source, 0);

        while(!q.isEmpty() && !u.equals(target)){
            u = this.getSmallestDistance(dist).getKey();
            q.remove(u);

            for(Node v : u.getNeighbours()){
                int alt = dist.get(u) + 1;//all connections have length 1
                if(alt<dist.get(v)){
                    dist.put(v, alt);

                    boolean match = false;
                    for(Edge edge : u.getEdges()){
                        if(!match &&
                                (  (edge.getFirstNode().equals(u) && edge.getSecondNode().equals(v))
                                || (edge.getFirstNode().equals(v) && edge.getSecondNode().equals(u))
                                )
                            ){
                            shortest.add(edge);
                            match = true;
                        }
                    }
                }
            }
        }
        return shortest;
    }

    private Map.Entry<Node, Integer> getSmallestDistance(HashMap<Node, Integer> dist){
        Map.Entry<Node, Integer> minEntry = null;

        for (Map.Entry<Node, Integer> entry : dist.entrySet())
        {
            if (minEntry == null || entry.getValue().compareTo(minEntry.getValue()) < 0)
            {
                minEntry = entry;
            }
        }
        return minEntry;
    }
}
