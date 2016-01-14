package migration;

import cluster.Cable;
import cluster.Cluster;
import cluster.Server;
import graph.Edge;
import graph.Graph;
import graph.Node;
import vm.VM;

import java.util.List;

public interface MigrationPolicy {

    List<Migration> update(Cluster<Node, Cable> cluster);

    Server allocateVM(VM vm, Cluster<Node, Cable> cluster);

}
