package vm;

import cluster.Path;
import lombok.Data;
import lombok.NonNull;
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
    @NonNull private int vCPU;

    private final double MAX_CPU = 100* vCPU;

    /**
     * The amount of RAM, in GiB.
     */
    @NonNull private int maxRAM;
    /**
     * The maximum bandwidth of the internet connection, in Mbps.
     */
    @NonNull private int maxBandwidth;

    /** @invariant 0 <= CPU <= MAX_CPU**/
    private int CPU;
    private List<Path> paths = new ArrayList<Path>();

    public void tick() {
        //TODO
    }
}
