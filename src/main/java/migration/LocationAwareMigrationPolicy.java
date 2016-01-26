package migration;

import cluster.Cable;
import cluster.Cluster;
import cluster.Server;
import graph.Node;
import switches.Switch;
import vm.VM;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class LocationAwareMigrationPolicy extends MigrationPolicy {

    private double upperThreshold;

    private final static Logger logger = Logger.getLogger(LocationAwareMigrationPolicy.class.getName());

    public LocationAwareMigrationPolicy(double upperThreshold){
        this.upperThreshold = upperThreshold;
    }

    @Override
    Server determineMigrationTarget(VM vm, Cluster<Node, Cable> cluster) {
        Server target = null;
        Server source = vm.getServer();
        int bestFreeCPU = 0;

        Set<Server> groupedServers = vm.getGroup().getVms().stream().map(VM::getServer).collect(Collectors.toSet());
        groupedServers.remove(vm.getServer());

        //IF GROUPED FIND SERVER IN GROUP
        for(Server server : groupedServers){
            int freeCPU = server.getCPU()- server.getCPU();
            if(freeCPU - vm.getCPU() > 0){//if there are enough cores available
                if(freeCPU > bestFreeCPU){//if it is a better fit
                    target = server;
                    bestFreeCPU = freeCPU;
                }
            }
        }
        if(target != null){
            return target;
        }

        Node tor = (Node) source.getNeighbours().toArray()[0];

        //ELSE CHECK SERVERS IN SAME RACK
        Set<Server> rack = tor
                .getNeighbours().stream()
                .filter(node -> node instanceof Server)
                .map(node -> (Server) node).collect(Collectors.toSet());
        rack.remove(source);

        for(Server server : rack){
            int freeCPU = server.getCPU()- server.getCPU();
            if(freeCPU - vm.getCPU() > 0){//if there are enough cores available
                if(freeCPU > bestFreeCPU){//if it is a better fit
                    target = server;
                    bestFreeCPU = freeCPU;
                }
            }
        }
        if(target != null){
            return target;
        }

        //ELSE CHECK SERVERS IN SAME CLUSTER
        Set<Switch> switches = new TreeSet<>();
        Set<Server> clusterServers  = new TreeSet<>();
        tor.getNeighbours()//twice-removed switches (hubs)
                .stream().map(node -> (node instanceof Switch && !node.equals(tor)) ? node.getNeighbours() : Collections.EMPTY_SET).map(ss -> switches.addAll(ss));//thrice-removed switches (gateway and TORs)
        switches.remove(tor);//prevent loopback
        switches.stream().map(node -> node.getNeighbours().stream().map(neighbour -> (neighbour instanceof Server) ? clusterServers.add((Server) neighbour) : true));//servers linked to thrice-removed switches

        for(Server server : clusterServers){
            int freeCPU = server.getCPU()- server.getCPU();
            if(freeCPU - vm.getCPU() > 0){//if there are enough cores available
                if(freeCPU > bestFreeCPU){//if it is a better fit
                    target = server;
                    bestFreeCPU = freeCPU;
                }
            }
        }
        if(target != null){
            return target;
        }

        //ELSE BEST FIT
        List<Server> possibleServers = cluster.getServers();// Get all possible targets
        for(Server server : possibleServers){
            int freeCPU = server.getCPU()- server.getCPU();
            if(freeCPU - vm.getCPU() > 0){//if there are enough cores available
                if(freeCPU > bestFreeCPU){//if it is a better fit
                    target = server;
                    bestFreeCPU = freeCPU;
                }
            }
        }
        return target;
    }

    @Override
    Set<VM> determineVMsToMigrate(Server server) {
        //NON-GROUPED FIRST
        Set<VM> result = new HashSet<>();
        if(server.getNonMigratingCPU()+server.getReservedCPU() > server.MAX_CPU*upperThreshold) {
            logger.fine("Server has exceeded upper threshold: " + server);

            while(server.getNonMigratingVMs().size() > 1 && server.getNonMigratingCPU()+server.getReservedCPU() > server.MAX_CPU*upperThreshold){

                // If possible pick non-grouped
                VM bestVM = null;
                for(VM vm : server.getVms()){
                    if(vm.getState().equals(VM.State.RUNNING) && vm.getGroup() == null && (bestVM == null || vm.getCPU() > bestVM.getCPU())){
                        bestVM = vm;
                    }
                }
                if(bestVM != null){
                    bestVM.setState(VM.State.MIGRATING);
                }else{
                    //Else pick largest
                    bestVM = super.selectLargest(server);
                }
                result.add(bestVM);
                logger.fine("VM found to migrate: " + bestVM);
            }
        }
        return result;
    }
}
