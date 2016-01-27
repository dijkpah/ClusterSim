package migration;

import cluster.Server;
import vm.VM;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class LocationAwareMigrationPolicy2 extends LocationAwareMigrationPolicy {

    private double upperThreshold;

    private final static Logger logger = Logger.getLogger(LocationAwareMigrationPolicy2.class.getName());

    public LocationAwareMigrationPolicy2(double upperThreshold){
        super(upperThreshold);
        this.upperThreshold = upperThreshold;
    }

    //1. TRY GROUPED BUT NOT ON THIS SERVER
    //2. TRY NON-GROUPED
    //3. TRY LARGEST

    @Override
    Set<VM> determineVMsToMigrate(Server server) {
        //NON-GROUPED FIRST
        Set<VM> result = new HashSet<>();
        if(server.getNonMigratingCPU()+server.getReservedCPU() > server.MAX_CPU*upperThreshold) {
            logger.fine("Server has exceeded upper threshold: " + server);

            while(server.getNonMigratingVMs().size() > 1 && server.getNonMigratingCPU()+server.getReservedCPU() > server.MAX_CPU*upperThreshold){

                //PICK GROUPED (BUT NOT ON THIS SERVER)
                VM bestVM = null;
                for(VM vm : server.getVms()){
                    //FIND OUT OF THERE ARE MORE VMS OF THIS GROUP ON THE SERVER
                    boolean groupedOnServer = false;
                    if(vm.getGroup() != null) {
                        for (VM v : vm.getGroup().getVms()) {
                            if (!v.equals(vm) && v.getServer().equals(vm.getServer())) {
                                groupedOnServer = true;
                            }
                        }
                    }
                    if(vm.getState().equals(VM.State.RUNNING) && vm.getGroup() != null && !groupedOnServer && (bestVM == null || vm.getCPU() > bestVM.getCPU())){
                        bestVM = vm;
                    }
                }

                //ELSE TRY NON-GROUPED
                for(VM vm : server.getVms()){
                    if(vm.getState().equals(VM.State.RUNNING) && vm.getGroup() == null && (bestVM == null || vm.getCPU() > bestVM.getCPU())){
                        bestVM = vm;
                    }
                }

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
}
