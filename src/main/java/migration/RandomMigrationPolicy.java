package migration;

import cluster.Cable;
import cluster.Cluster;
import cluster.Server;
import graph.Node;
import vm.VM;

import java.util.*;
import java.util.logging.Logger;

/**
 * Migration policy which does random migrations
 */
public class RandomMigrationPolicy extends MigrationPolicy {

    private Random random = new Random(2L);

    private final static Logger logger = Logger.getLogger(RandomMigrationPolicy.class.getName());

    private double upperThreshold;

    public RandomMigrationPolicy(double upperThreshold){
        this.upperThreshold = upperThreshold;
    }

    @Override
    Server determineMigrationTarget(VM vm, Cluster<Node, Cable> cluster) {
        logger.fine("Allocating VM " + vm);

        // Get all possible targets
        List<Server> possibleServers = cluster.getServers();

        // Find a target if there are possible targets
        Server target = null;
        if(possibleServers.size() > 0 && !(possibleServers.size()==1 && possibleServers.get(0).equals(vm.getServer()))){
            // Make sure it is not the same server
            while(target==null || target.equals(vm.getServer())){
                target =  possibleServers.get(random.nextInt(possibleServers.size()));
            }
        }

        return target;
    }

    @Override
    public Set<VM> determineVMsToMigrate(Server server) {
        Set<VM> result = new HashSet<>();
        if(server.getRunningCPU() > server.MAX_CPU*upperThreshold) {
            logger.fine("Server has exceeded upper threshold: " + server);

            while(server.getNonMigratingVMs().size() > 1 && server.getRunningCPU() > server.MAX_CPU*upperThreshold){
                // Pick one with largest CPU
                VM bestVM = super.selectLargest(server);
                if(bestVM != null){
                    bestVM.setState(VM.State.MIGRATING);
                    result.add(bestVM);
                    logger.fine("VM found to migrate: " + bestVM);
                }
            }
        }
        return result;
    }

    public String toString(){
        return "Random migration policy with upper Threshold "+upperThreshold;
    }
}
