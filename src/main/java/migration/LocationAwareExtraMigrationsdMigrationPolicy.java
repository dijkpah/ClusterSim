package migration;

import cluster.Server;
import vm.VM;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class LocationAwareExtraMigrationsdMigrationPolicy extends LocationAwareMigrationPolicy2 {

    private double upperThreshold;

    private final static Logger logger = Logger.getLogger(LocationAwareExtraMigrationsdMigrationPolicy.class.getName());

    public LocationAwareExtraMigrationsdMigrationPolicy(double upperThreshold){
        super(upperThreshold);
    }

    public static final double migrationchance = 0.01;

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
                chosenMigration = vm;
            }
        }
        result.addAll(super.determineVMsToMigrate(server));
        return result;
    }
}
