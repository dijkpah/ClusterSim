package vm;

import graph.Path;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString;
import simulation.LoadGenerator;
import simulation.NormalLoadGenerator;
import simulation.Params;
import simulation.SimulationEntity;

import java.util.ArrayList;
import java.util.List;

// Each VM type is characterized by processing performance defined in MIPS, RAM
// capacity, storage capacity and network bandwidth
@Data
@ToString(exclude={"paths","loadGenerator"})
public abstract class VM implements SimulationEntity {

    @NonNull public final int id;

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

    private LoadGenerator loadGenerator;

    /** @invariant 0 <= CPU <= MAX_CPU()**/
    public VM(int id, int vCPUs, int maxRAM, int maxBandwidth){
        this.id = id;
        this.vCPUs = vCPUs;
        this.maxRAM = maxRAM;
        this.maxBandwidth = maxBandwidth;
        this.CPU = (int)(Params.INITIAL_VM_CPU_USAGE * this.MAX_CPU());
        this.loadGenerator = new NormalLoadGenerator();
    }

    private int CPU;
    private List<Path> paths = new ArrayList<Path>();

    private void fluctuateLoad(){
        this.CPU = (int) loadGenerator.generate(this.CPU, 0, this.MAX_CPU());
    }

    public void tick() {
        this.fluctuateLoad();
        System.out.println("Tick " + this.toString());
        //TODO
    }

    public enum State{
        RUNNING,
        MIGRATING,
        RESERVED
    }

}
