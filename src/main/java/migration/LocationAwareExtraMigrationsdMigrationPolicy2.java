package migration;

import cluster.Server;
import vm.VM;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class LocationAwareExtraMigrationsdMigrationPolicy2 extends LocationAwareMigrationPolicy2 {

    private double upperThreshold;

    private final static Logger logger = Logger.getLogger(LocationAwareExtraMigrationsdMigrationPolicy2.class.getName());

    public LocationAwareExtraMigrationsdMigrationPolicy2(double upperThreshold){
        super(upperThreshold);
        this.upperThreshold = upperThreshold;
    }

    public static final double migrationchance = 0.1;

    @Override
    Set<VM> determineVMsToMigrate(Server server) {
        Set<VM> result = new HashSet<>();

        VM chosenMigration = null;
        //PICK GROUPED (BUT NOT ON THIS SERVER)
        for (VM vm : server.getVms()) {
            //FIND OUT OF THERE ARE MORE VMS OF THIS GROUP ON THE SERVER
            boolean groupedOnServer = false;
            if (vm.getGroup() != null) {
                for (VM v : vm.getGroup().getVms()) {
                    if (!v.equals(vm) && v.getServer().equals(vm.getServer())) {
                        groupedOnServer = true;
                    }
                }
            }
            if (vm.getState().equals(VM.State.RUNNING) && chosenMigration == null && vm.getGroup() != null && !groupedOnServer && Math.random() < migrationchance) {
                result.add(vm);
                vm.setState(VM.State.MIGRATING);
                chosenMigration = vm;
            }
        }
        result.addAll(super.determineVMsToMigrate(server));
        return result;
    }
    public String toString(){
        return "Location aware 2 nongrouped migration policy with upper Threshold "+upperThreshold+" and "+migrationchance+" chance on extra migration";
    }
}
