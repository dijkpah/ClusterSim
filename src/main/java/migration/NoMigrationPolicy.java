package migration;

import cluster.Cable;
import cluster.Cluster;
import cluster.Server;
import graph.Edge;
import graph.Graph;
import graph.Node;
import vm.VM;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Migration policy which does not do migrations
 */
public class NoMigrationPolicy implements MigrationPolicy{
    public List<Migration> update(Cluster<Node, Cable> cluster) {
        return new ArrayList<Migration>();
    }

    @Override
    public Server allocateVM(VM vm, Cluster<Node, Cable> cluster) {
        return null;
    }

    @Override
    public Set<VM> determineVMsToMigrate(Server server) {
        return null;
    }
}
