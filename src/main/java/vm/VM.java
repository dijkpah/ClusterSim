package vm;

import lombok.Data;
import lombok.NonNull;

// Each VM type is characterized by processing performance defined in MIPS, RAM
// capacity, storage capacity and network bandwidth
@Data
public class VM {
    /**
     * The number of instructions available per second (=clock time * number of cores).
     */
    @NonNull private long maxCPU;
    /**
     * The amount of RAM, in GiB.
     */
    @NonNull private int maxRAM;
    /**
     * The maximum bandwidth of the internet connection, in Mbps.
     */
    @NonNull private int maxBandwidth;
}
