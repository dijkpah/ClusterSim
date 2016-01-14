package migration;

import cluster.Cable;
import cluster.Cluster;
import cluster.Server;
import graph.Edge;
import graph.Graph;
import graph.Node;
import vm.VM;

import java.util.List;
import java.util.Set;

public interface MigrationPolicy {

    List<Migration> update(Cluster<Node, Cable> cluster);

    /**
     * Allocate the given vm to a new server.
     * @param vm
     * @param cluster
     * @return
     */
    Server allocateVM(VM vm, Cluster<Node, Cable> cluster);

    Set<VM> determineVMsToMigrate(Server server);

}
