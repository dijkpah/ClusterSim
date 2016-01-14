package simulation;

public class Params {
    /**
     * The initial CPU usage of a VM.
     */
    public static final double INITIAL_VM_CPU_USAGE = 0.5;
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
}
