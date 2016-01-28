package migration;

import cluster.Cable;
import cluster.Cluster;
import cluster.Server;
import graph.Node;
import vm.VM;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

public class LargestBestFitMigrationPolicy extends MigrationPolicy {

    private double upperThreshold;

    private final static Logger logger = Logger.getLogger(LargestBestFitMigrationPolicy.class.getName());

    public LargestBestFitMigrationPolicy(double upperThreshold){
        this.upperThreshold = upperThreshold;
    }

    @Override
    Server determineMigrationTarget(VM vm, Cluster<Node, Cable> cluster) {
        Server target = null;
        Server source = vm.getServer();
        int bestFreeCPU = 0;

        //ELSE BEST FIT
        Set<Server> possibleServers = new TreeSet<>();
        possibleServers.addAll(cluster.getServers());// Get all possible targets
        possibleServers.remove(source);
        for(Server server : possibleServers){
            int freeCPU = server.MAX_CPU - server.getCPU()- vm.getCPU();
            if(freeCPU - vm.getCPU() > 0 && !server.equals(source)){//if there are enough cores available
                if(freeCPU > bestFreeCPU){//if it is a better fit
                    target = server;
                    bestFreeCPU = freeCPU;
                }
            }
        }
        if(source.equals(target)){
            return null;
        }
        return target;
    }


    //TRY LARGEST

    @Override
    Set<VM> determineVMsToMigrate(Server server) {
        Set<VM> result = new HashSet<>();
        if(server.getNonMigratingCPU()+server.getReservedCPU() > server.MAX_CPU*upperThreshold) {
            logger.fine("Server has exceeded upper threshold: " + server);

            while(server.getNonMigratingVMs().size() > 1 && server.getNonMigratingCPU()+server.getReservedCPU() > server.MAX_CPU*upperThreshold){
                VM bestVM = null;

                if(bestVM == null){
                    //Else pick largest
                    bestVM = super.selectLargest(server);
                }
                if(bestVM != null) {
                    bestVM.setState(VM.State.MIGRATING);
                }
                result.add(bestVM);
                logger.fine("VM found to migrate: " + bestVM);
            }
        }
        return result;
    }


    public String toString(){
        return "Largest Best Fit migration policy with upper Threshold "+upperThreshold;
    }
}
