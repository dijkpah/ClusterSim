package migration;

import cluster.Cable;
import cluster.Cluster;
import cluster.Server;
import graph.Node;
import vm.VM;

import java.util.*;
import java.util.logging.Logger;

/**
 * Migration policy which does not do migrations
 */
public class RandomMigrationPolicy extends MigrationPolicy {
    private final static Logger logger = Logger.getLogger(RandomMigrationPolicy.class.getName());

    private double upperThreshold;

    public RandomMigrationPolicy(double upperThreshold){
        this.upperThreshold = upperThreshold;
    }

    @Override
    Server determineMigrationTarget(VM vm, Cluster<Node, Cable> cluster) {
        logger.fine("Allocating VM " + vm);
        Random random = new Random();

        // Get all possible targets
        List<Server> possibleServers = cluster.getPossibleTargetServers();

        // Find a target if there are possible targets
        Server target = null;
        if(possibleServers.size() > 0){
            // Make sure it is not the same server
            while(target==null || target.equals(vm.getServer())){
                target =  possibleServers.get(random.nextInt(possibleServers.size()));
            }
        }

        return target;
    }

    @Override
    public Set<VM> determineVMsToMigrate(Server server) {
        Set<VM> result = null;
        if(server.getRunningCPU() > server.MAX_CPU*upperThreshold) {
            logger.fine("Server has exceeded upper threshold: " + server);

            result = new HashSet<>();

            while(server.getRunningVMs().size() > 1 && server.getRunningCPU() > server.MAX_CPU*upperThreshold){
                // Pick one with largest CPU
                VM bestVM = null;
                for(VM vm : server.getVms()){
                    if(vm.getState().equals(VM.State.RUNNING) && (bestVM == null || vm.getCPU() > bestVM.getCPU())){
                        bestVM = vm;
                    }
                }
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
