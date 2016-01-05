package vm;

import cluster.Connection;
import cluster.Path;
import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

// Each VM type is characterized by processing performance defined in MIPS, RAM
// capacity, storage capacity and network bandwidth
@Data
public class VM {
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

    private int CPU;
    private List<Path> paths = new ArrayList<Path>();

    public int getCPU() {
        return CPU;
    }
}
