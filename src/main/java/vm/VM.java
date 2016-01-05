package vm;

import graph.Path;
import lombok.Data;
import lombok.NonNull;
import simulation.Params;
import simulation.SimulationEntity;

import java.util.ArrayList;
import java.util.List;

// Each VM type is characterized by processing performance defined in MIPS, RAM
// capacity, storage capacity and network bandwidth
@Data
public abstract class VM implements SimulationEntity {
    /**
     * The number of instructions available per second (=clock time * number of cores).
     */
    @NonNull public final int vCPUs;

    public int MAX_CPU(){
        return this.vCPUs * 100;
    }

    /**
     * The amount of RAM, in GiB.
     */
    @NonNull private int maxRAM;
    /**
     * The maximum bandwidth of the internet connection, in Mbps.
     */
    @NonNull private int maxBandwidth;

    /** @invariant 0 <= CPU <= MAX_CPU()**/
    public VM(int vCPUs, int maxRAM, int maxBandwidth){
        this.vCPUs = vCPUs;
        this.maxRAM = maxRAM;
        this.maxBandwidth = maxBandwidth;
        this.CPU = (int)(Params.INITIAL_VM_CPU_USAGE * this.MAX_CPU());
    }

    private int CPU;
    private List<Path> paths = new ArrayList<Path>();

    public void tick() {
        System.out.println("Tick " + this.toString());
        //TODO
    }

}
