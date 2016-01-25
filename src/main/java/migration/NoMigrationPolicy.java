package migration;

import cluster.Cable;
import cluster.Cluster;
import cluster.Server;
import graph.Node;
import vm.VM;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Migration policy which does not do migrations
 */
public class NoMigrationPolicy extends MigrationPolicy{
    @Override
    Server determineMigrationTarget(VM vm, Cluster<Node, Cable> cluster) {
        return null;
    }

    @Override
    public Set<VM> determineVMsToMigrate(Server server) {
        return null;
    }

    public String toString(){
        return "No migrations";
    }
}
