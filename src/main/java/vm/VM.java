package vm;

import lombok.Data;
import lombok.NonNull;
import simulation.SimulationEntity;

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

    public void tick() {
        //TODO
    }
}
