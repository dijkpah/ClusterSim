package simulation;

import cluster.Cable;
import cluster.Cluster;
import cluster.Connection;
import cluster.Server;
import graph.Node;
import lombok.Data;
import lombok.NonNull;
import migration.Migration;
import migration.MigrationPolicy;
import vm.VM;

import java.io.IOException;
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

    /**
     * The total number of migrations in the last tick
     */
    private int totalMigrations;

    /**
     * The number of remaining (uncompleted) migrations in the last tick
     */
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
            logger.info("== TICK " + clock + " ==");

            // Reset the cluster for the new tick
            // This mainly resets the traffic on the cables
            cluster.reset();

            // The idea is that all calculations are executed at the start of the tick
            // (updating load, determination of migrations, deciding which server to let sleep or wake up)
            // while the remaining time of the tick is spent for the network traffic and the waking up/falling asleep of servers

            // Step 1: Update server states
            updateServerStates();

            // Step 2: Update load and network traffic and apply this to the network
            cluster.tick();

            // Step 3: Determine migrations
            currentMigrations.addAll(migrationPolicy.update(cluster));

            // Step 4: Apply migrations to the network
            setTotalMigrations(currentMigrations.size());
            executeMigrations();
            setRemainingMigrations(currentMigrations.size());

            // Do some logging
            logger.finer("Total migrations: " + getTotalMigrations());
            logger.finer("Remaining migrations: " + getRemainingMigrations());
            logger.finer("Edges: " + cluster.getEdges());

            // Update the log
            updateLog();

            // Increase the clock
            clock++;
        }
    }

    private void updateServerStates() {
        for(Server server : cluster.getServers()){
            if(server.getState().equals(Server.State.FALLING_ASLEEP)){
                server.setState(Server.State.SLEEPING);
            }

            if(server.getState().equals(Server.State.WAKING_UP)){
                server.setState(Server.State.AVAILABLE);
                logger.fine("Server woke up: " + server);
            }

            if(server.getState().equals(Server.State.SHOULD_WAKE_UP)){
                server.setState(Server.State.WAKING_UP);
            }

            // If there is nothing on the server, let it fall asleep
            if(server.getState().equals(Server.State.AVAILABLE) && server.getNonMigratingVMs().size() == 0 && server.getReservedVMs().size() == 0){
                server.setState(Server.State.FALLING_ASLEEP);
                logger.fine("Letting server falling asleep: " + server);
            }
        }
    }
    public Map<String, String> params(){
        Map<String, String> params = new TreeMap<>();
        params.put("Initial VM CPU Usage", ""+Params.INITIAL_VM_CPU_USAGE);
        params.put("CPU load fluctuation deviation", ""+Params.CPU_LOAD_FLUCTUATION_DEVIATION);
        params.put("Cable Capacity (Mbps)", ""+Params.CABLE_CAPACITY);
        params.put("Tick duration (s)", ""+Params.TICK_DURATION);
        params.put("Tick Count", ""+Params.TICK_COUNT);
        params.put("Percentage network traffic between a VM and the world of bandwidth when 100% CPU", ""+Params.NETWORK_USAGE_VM_TO_WORLD_PERCENTAGE);
        params.put("Avg network traffic between connected VMs (Mbps)", ""+Params.NETWORK_USAGE_VM_TO_VM_AVERAGE);
        params.put("Migration Policy", Params.MIGRATION_POLICY.toString());
        return params;
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
        logger.finer(migration.toString());

        logger.finer("Migration: " + migration);

        switch (migration.getTo().getState()){
            case AVAILABLE:
                // Get the connection
                Connection connection = cluster.getConnection(Connection.Type.MIGRATION, migration.getFrom(), migration.getTo());

                if(connection == null || connection.getEdges() == null || connection.getEdges().size() == 0){
                    throw new RuntimeException("I can't execute an impossible migration! " + connection);
                }

                // Determine remaining bandwidth
                int bandwidth = connection.getBandwidth() - connection.getNetworkTraffic();
                // Determine used bandwidth and make sure it is rounded up (otherwise there is a small remaining fraction of the VM still in need of transfer).
                bandwidth = Math.min(bandwidth, (int)Math.ceil((migration.getVm().getSize() - migration.getTransferredData())/(double)Params.TICK_DURATION));
                // Use this bandwidth
                connection.addNetworkTraffic(bandwidth);
                // Update the cables
                connection.applyNetworkTraffic();
                // Determine transferred bytes
                migration.setTransferredData(migration.getTransferredData() + Params.TICK_DURATION * bandwidth);


                // If all data is transferred, the migration is completed
                //logger.fine(migration.getTransferredData());
                //logger.fine(migration.getVm().getSize());
                if (migration.isCompleted()) {
                    logger.finer("Migration completed!");
                    migration.getFrom().removeVM(migration.getVm());
                    migration.getTo().removeReservedVM(migration.getVm());
                    migration.getTo().addVM(migration.getVm());
                    migration.getVm().setState(VM.State.RUNNING);
                }
                break;
            case SLEEPING:
                // Wake up the server
                migration.getTo().setState(Server.State.SHOULD_WAKE_UP);
                logger.finer("Server is sleeping, waking up: " + migration.getTo());
                break;
            default:
                // otherwise: wait until the server is sleeping or
                logger.finer("Can't do anything right now, server is not available or sleeping");
                break;
        }
    }

    /**
     * Enters power consumption summations for servers and connections
     */
    private void updateLog() {
        excelLogger.tick(this);
    }


    public static void main(String[] args) {
        // Create the simulation
        ClusterSimulation simulation = new ClusterSimulation(Params.CLUSTER, Params.MIGRATION_POLICY);

        // Setup logging
        simulation.setupLogging();

        //Set time
        int ticks = Params.TICK_COUNT;

        // Start
        simulation.run(ticks);

        // Create Graph
        simulation.getExcelLogger().makeGraph(simulation.getOutputFileName(), simulation.params());
    }

    protected String getOutputFileName() {
        return Params.OUTPUT_FILE_PREFIX + this.getCluster().getName() + "." + Params.OUTPUT_FILE_EXTENTION;
    }

    protected String getLogFileName() {
        return Params.OUTPUT_FILE_PREFIX + this.getCluster().getName() + ".log";
    }

    public void setupLogging() {
        Logger globalLogger = LogManager.getLogManager().getLogger("");
        Handler handler = null;
        try {
            handler = new FileHandler(this.getLogFileName());
        } catch (IOException e) {
            e.printStackTrace();
            handler = new ConsoleHandler();
        }
        globalLogger.setLevel(Params.LOG_LEVEL);
        for (Handler defaultHandler : globalLogger.getHandlers()) {
            globalLogger.removeHandler(defaultHandler);
        }
        handler.setLevel(Params.LOG_LEVEL);
        handler.setFormatter(new ClusterSimLogFormatter());
        globalLogger.addHandler(handler);
    }
}
