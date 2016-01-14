package migration;

import cluster.Cable;
import cluster.Cluster;
import cluster.Server;
import graph.Node;
import vm.VM;

import java.util.*;

/**
 * Migration policy which does not do migrations
 */
public class RandomMigrationPolicy implements MigrationPolicy{

    private double upperThreshold;

    public RandomMigrationPolicy(double upperThreshold){
        this.upperThreshold = upperThreshold;
    }

    public List<Migration> update(Cluster<Node, Cable> cluster) {
        List<Migration> result = new ArrayList<Migration>();

        for(Node node : cluster.getNodes()){
            if(node instanceof Server){
                Server server = (Server) node;
                if(server.getNonMigratingCPU() > server.MAX_CPU*upperThreshold){
                    // Migrate some VMs
                    Set<VM> toMigrate = determineVMsToMigrate(server);
                    for(VM vm : toMigrate){
                        VM target = vm.createReservedSpace();
                        Server targetServer = allocateVM(target, cluster);
                        result.add(new Migration(server, targetServer, vm, target, 0));
                    }
                }
            }
        }

        return result;
    }

    public Server allocateVM(VM vm, Cluster<Node, Cable> cluster) {
        Random random = new Random();
        Server result = null;
        while(result == null){
            Node node = cluster.getNodes().get(random.nextInt(cluster.getNodes().size()));
            if(node instanceof Server){
                result = (Server)node;
            }
        }
        return result;
    }

    @Override
    public Set<VM> determineVMsToMigrate(Server server) {
        Set<VM> result = new HashSet<VM>();
        int removed = 0;
        while(server.getNonMigratingCPU() > server.MAX_CPU*upperThreshold){
            // Pick one with largest CPU
            VM bestVM = null;
            for(VM vm : server.getVms()){
                if(vm.getState().equals(Thread.State.RUNNABLE) && (bestVM == null || vm.getCPU() > bestVM.getCPU())){
                    bestVM = vm;
                }
            }
            if(bestVM != null){
                result.add(bestVM);
                removed += bestVM.getCPU();
            }
        }
        return result;
    }
}
