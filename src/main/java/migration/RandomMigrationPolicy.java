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
public class RandomMigrationPolicy implements MigrationPolicy{
    private final static Logger logger = Logger.getLogger(RandomMigrationPolicy.class.getName());

    private double upperThreshold;

    public RandomMigrationPolicy(double upperThreshold){
        this.upperThreshold = upperThreshold;
    }

    public List<Migration> update(Cluster<Node, Cable> cluster) {
        List<Migration> result = new ArrayList<>();

        for(Node node : cluster.getNodes()){
            if(node instanceof Server){
                Server server = (Server) node;
                if(server.getRunningCPU() > server.MAX_CPU*upperThreshold){
                    logger.fine("Server has exceeded upper threshold: " + server);
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
        logger.fine("Allocating VM " + vm);
        Random random = new Random();

        List<Server> possibleServers = cluster.getPossibleTargetServers();

        return possibleServers.size() > 0 ? possibleServers.get(random.nextInt(possibleServers.size())) : null;
    }

    @Override
    public Set<VM> determineVMsToMigrate(Server server) {
        Set<VM> result = new HashSet<>();
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
        return result;
    }

    public String toString(){
        return "Random migration policy with upper Threshold "+upperThreshold;
    }
}
