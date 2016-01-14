package graph;

import lombok.Data;
import lombok.NonNull;
import simulation.SimulationEntity;

import java.util.ArrayList;
import java.util.List;

@Data
public class Path implements SimulationEntity {
    @NonNull private List<Edge> edges;
    @NonNull private Node firstEndPoint;
    @NonNull private Node secondEndPoint;

    public void tick() {
    }

    public Path(Node firstEndPoint, Node secondEndPoint) {
        this.firstEndPoint = firstEndPoint;
        this.secondEndPoint = secondEndPoint;

        this.edges = findShortestPath(firstEndPoint, secondEndPoint);
    }

    /**
     * Return the edges in the shortest path between the two endpoints, or null if such a path does not exist.
     *
     * @param firstEndPoint  The start node
     * @param secondEndPoint The end node
     * @return The edges in the shortest path between the two endpoints, or null if such a path does not exist.
     */
    private List<Edge> findShortestPath(Node firstEndPoint, Node secondEndPoint) {
        // Base case: first endpoint is the same as the second endpoint
        if (secondEndPoint.equals(firstEndPoint)) {
            return new ArrayList<Edge>();
        } else {
            List<Edge> shortest = null;

            // Loop through all edges, and use the one which yields the shortest path
            for (Edge edge : firstEndPoint.getEdges()) {
                List<Edge> result;
                if (edge.getFirstNode().equals(firstEndPoint)) {
                    result = findShortestPath(edge.getSecondNode(), secondEndPoint);
                } else {
                    result = findShortestPath(edge.getFirstNode(), secondEndPoint);
                }

                // Compare the found path to the shortest
                if (result != null && shortest == null || result.size() < shortest.size()-1) {
                    result.add(edge);
                    shortest = result;
                }
            }

            return shortest;
        }
    }
}
