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
public class VM implements SimulationEntity {
    /**
     * The number of instructions available per second (=clock time * number of cores).
     */
    @NonNull private int maxCPU;
    /**
     * The amount of RAM, in GiB.
     */
    @NonNull private int maxRAM;
    /**
     * The maximum bandwidth of the internet connection, in Mbps.
     */
    @NonNull private int maxBandwidth;

    public VM(int maxCPU, int maxRAM, int maxBandwidth){
        this.maxCPU = maxCPU;
        this.maxRAM = maxRAM;
        this.maxBandwidth = maxBandwidth;
        this.CPU = (int)(Params.INITIAL_VM_CPU_USAGE * maxCPU);
    }

    private int CPU;
    private List<Path> paths = new ArrayList<Path>();

    public void tick() {
        System.out.println("Tick " + this.toString());
        //TODO
    }
}
