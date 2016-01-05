package migration;

import cluster.Server;
import graph.Edge;
import graph.Graph;
import graph.Node;
import vm.VM;

import java.util.List;

public interface MigrationPolicy {

    List<Migration> update(Graph<Node, Edge> graph);

    Server allocateVM(VM vm, Graph<Node, Edge> graph);

}
