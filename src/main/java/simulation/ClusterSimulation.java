package simulation;

import cluster.*;
import graph.Node;
import lombok.Data;
import lombok.NonNull;
import migration.Migration;
import migration.MigrationPolicy;
import switches.MainSwitch;
import switches.Switch;
import vm.M4LargeVM;
import vm.M4XLargeVM;
import vm.VM;

import java.util.*;
import java.util.logging.*;

/**
 * A simulation of a cluster
 */
@Data
public class ClusterSimulation {
    /**
     * The cluster this simulation runs on.
     */
    @NonNull
    private Cluster<Node, Cable> cluster;

    /**
     * The MigrationPolicy to use in this simulation.
     */
    @NonNull
    private MigrationPolicy migrationPolicy;

    private final static Logger logger = Logger.getLogger(ClusterSimulation.class.getName());

    public long clock;
    private List<Migration> currentMigrations = new ArrayList<>();
    private ExcelLogger excelLogger = new ExcelLogger();

    /** The total number of migrations in the last tick */
    private int totalMigrations;

    /** The number of remaining (uncompleted) migrations in the last tick */
    private int remainingMigrations;

    /**
     * Start the simulation
     */
    public void run(int ticks) {
        logger.info("Starting new simulation");
        logger.info("Cluster: " + cluster.toString());
        logger.info("Migration policy: " + migrationPolicy.toString());
        logger.info("Number of ticks: " + ticks);

        clock = 0;
        while (clock < ticks) {
            logger.fine("== TICK " + clock + " ==");

            // Reset the cluster for the new tick
            // This mainly resets the traffic on the cables
            cluster.reset();

            // Step 1 and 2 happen at the start of the tick,

            // Step 1: Update load and network traffic
            cluster.tick();

            // Step 2: Determine migrations
            currentMigrations.addAll(migrationPolicy.update(cluster));

            // Step 3: Apply migrations
            setTotalMigrations(currentMigrations.size());
            executeMigrations();
            setRemainingMigrations(currentMigrations.size());

            logger.finer("Total migrations: " + getTotalMigrations());
            logger.finer("Remaining migrations: " + getRemainingMigrations());
            logger.finer("Edges: " + cluster.getEdges());

            // Update states of servers

            // Update connections of migrated VMS

            // Update the log
            updateLog();
            clock++;
        }
    }

    private void executeMigrations() {
        logger.fine("Executing migrations");

        for (Migration migration : currentMigrations) {
            executeMigration(migration);
        }

        // Remove finished migrations
        this.currentMigrations.removeIf(m -> m.getTransferredData() >= m.getVm().getSize());
    }

    private void executeMigration(Migration migration) {
        // Update the state of the VMs
        migration.getVm().setState(VM.State.MIGRATING);
        migration.getTargetVM().setState(VM.State.RESERVED);

        logger.finer(migration.toString());

        // Get the connection
        Connection connection = cluster.getConnection(Connection.Type.MIGRATION, migration.getFrom(), migration.getTo());
        // Determine remaining bandwidth
        int bandwidth = connection.getBandwidth() - connection.getNetworkTraffic();
        // Use all remaining bandwidth
        connection.addNetworkTraffic(bandwidth);
        // Update the cables
        connection.applyNetworkTraffic();
        // Determine transferred bytes
        migration.setTransferredData(migration.getTransferredData() + Params.TICK_DURATION * bandwidth);


        // If all data is transferred, the migration is completed
        //logger.fine(migration.getTransferredData());
        //logger.fine(migration.getVm().getSize());
        if (migration.getTransferredData() >= migration.getVm().getSize()) {
            logger.finer("Migration completed: " + migration);
            migration.getFrom().removeVM(migration.getVm());
            migration.getTo().removeVM(migration.getTargetVM());
            migration.getTo().addVM(migration.getVm());
            migration.getVm().setState(VM.State.RUNNING);
        }
    }

    /**
     * Enters power consumption summations for servers and connections
     */
    private void updateLog() {
        excelLogger.tick(this);
    }



    public static void main(String[] args) {
        // Setup logging
        setupLogging();

        // Create the simulation
        ClusterSimulation simulation = new ClusterSimulation(Params.CLUSTER, Params.MIGRATION_POLICY);

        //Set time
        int ticks = Params.TICK_COUNT;

        // Start
        simulation.run(ticks);

        //Create Graph
        simulation.getExcelLogger().makeGraph();
    }

    private static void setupLogging(){
        Logger globalLogger = LogManager.getLogManager().getLogger("");
        Handler handler = new ConsoleHandler();
        globalLogger.setLevel(Params.LOG_LEVEL);
        for(Handler defaultHandler : globalLogger.getHandlers()){
            globalLogger.removeHandler(defaultHandler);
        }
        handler.setLevel(Params.LOG_LEVEL);
        handler.setFormatter(new ClusterSimLogFormatter());
        globalLogger.addHandler(handler);
    }
}
