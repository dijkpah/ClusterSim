package simulation;

import cluster.Cable;
import cluster.Cluster;
import cluster.ClusterFactory;
import graph.Node;
import migration.MigrationPolicy;
import migration.RandomMigrationPolicy;

import java.util.logging.Level;

public class Params {
    /**
     * The initial CPU usage of a VM.
     */
    public static final double INITIAL_VM_CPU_USAGE = 0.5;
    /**
     * The possible percentage of CPU usage of a VM.
     */
    public static final double CPU_LOAD_POSSIBLE_MAX = 1.5;
    /**
     * The standard deviation of the cpu load fluctuation
     */
    public static final double CPU_LOAD_FLUCTUATION_DEVIATION = 0.10;
    /**
     * The capacity of a cable, in Mb/s.
     */
    public static final int CABLE_CAPACITY = 1000;
    /**
     * The duration of a tick in seconds.
     */
    public static final int TICK_DURATION = 15;
    /**
     * The average network traffic in Mb/s between a VM and the world.
     */
    //public static final int NETWORK_USAGE_VM_TO_WORLD_AVERAGE = 100;
    /**
     * The average network traffic in Mb/s between connected VMs.
     */
    public static final int NETWORK_USAGE_VM_TO_VM_AVERAGE = 50;

    /**
     * The percentage of the network usage of a vm to the world compared to the maximum bandwidth, when the CPULoad is 100%
     */
    public static final double NETWORK_USAGE_VM_TO_WORLD_PERCENTAGE = 0.5;


    public static final MigrationPolicy MIGRATION_POLICY = new RandomMigrationPolicy(0.4);
    public static final Cluster<Node,Cable> CLUSTER = ClusterFactory.simpleCluster();
    public static final int TICK_COUNT = 20;


    public static final String OUTPUT_FILE = "simulation.tsv";
    public static final String OUTPUT_SEPARATOR = "\t";
    public static final Level LOG_LEVEL = Level.ALL;

}
