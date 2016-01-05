package cluster;

import graph.Node;
import lombok.Data;

@Data
public class Server extends Node {

    /**
     * Based on:
     *
     * FIVE WAYS TO REDUCE DATA CENTER SERVER POWER CONSUMPTION
     * Blackburn 2008
     */
    public static final int MAX_POWER = 300;
    public static final int MIN_POWER = 200;

    /**Maximum possible CPU load in MIPS*/
    public static long MAX_CPU = 0;

    /**
     * Total MIPS done now
     * @invariant 0<= CPULoad <= MAX_CPU
     */
    private long CPULoad = 0;

    public double getPowerUsage(){
        return MIN_POWER + (CPULoad/MAX_CPU) * (MAX_POWER - MIN_POWER);
    }

    public Server(long MAX_CPU){
        this.MAX_CPU = MAX_CPU;
    }
}
