package migration;

import cluster.Cable;
import cluster.Cluster;
import cluster.Server;
import graph.Node;
import vm.VM;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class MigrationPolicy {

    public List<Migration> update(Cluster<Node, Cable> cluster){
        List<Migration> result = new ArrayList<>();

        for(Server server : cluster.getServers()){
            Set<VM> toMigrate = determineVMsToMigrate(server);

            // toMigrate can be null, indicating that no migrations take place from this server.
            if(toMigrate != null){
                for(VM vm : toMigrate){
                    // Determine the targetServer
                    Server targetServer = determineMigrationTarget(vm, cluster);

                    // If no target server could be found, this is null
                    // In this case, we can also not execute a migration
                    if(targetServer != null){
                        // Reserve the space on the server
                        targetServer.addReservedVM(vm);

                        // Set the state of the VM to migrationg
                        vm.setState(VM.State.MIGRATING);

                        // Create a new Migration
                        result.add(new Migration(server, targetServer, vm, 0));
                    }
                }
            }



        }

        return result;
    }

    /**
     * Determine the target server of a migration.
     * @param vm The VM to allocate.
     * @param cluster The cluster to containing the servers.
     * @return The server to allocate the VM to, or null if no server could be found.
     */
    abstract Server determineMigrationTarget(VM vm, Cluster<Node, Cable> cluster);

    /**
     * Given a server, determine whether to migration VMs and, when migrating, which VMs to migrate
     * @param server The server to determine of which VMs to migrate
     * @return null or a set of VMs
     */
    abstract Set<VM> determineVMsToMigrate(Server server);


    public VM selectLargest(Server server) {
        VM bestVM = null;
        for(VM vm : server.getVms()){
            if(vm.getState().equals(VM.State.RUNNING) && (bestVM == null || vm.getCPU() > bestVM.getCPU())){
                bestVM = vm;
            }
        }
        return bestVM;
    }

}
