package cluster;

import graph.Edge;
import graph.Node;
import lombok.Data;
import lombok.NonNull;
import simulation.SimulationEntity;

import java.util.List;

@Data
public class Path implements SimulationEntity {
    @NonNull private List<Edge> edges;
    @NonNull private Node firstEndPoint;
    @NonNull private Node secondEndPoint;

    public void tick() {
    }
}
