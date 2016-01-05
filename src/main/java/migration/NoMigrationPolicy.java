package migration;

import cluster.Server;
import graph.Edge;
import graph.Graph;
import graph.Node;
import vm.VM;

import java.util.ArrayList;
import java.util.List;

/**
 * Migration policy which does not do migrations
 */
public class NoMigrationPolicy implements MigrationPolicy{
    public List<Migration> update(Graph<Node, Edge> graph) {
        return new ArrayList<Migration>();
    }

    public Server allocateVM(VM vm, Graph<Node, Edge> graph) {
        return null;
    }
}
