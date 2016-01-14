package vm;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import simulation.*;

import java.util.HashMap;
import java.util.Map;

// Each VM type is characterized by processing performance defined in MIPS, RAM
// capacity, storage capacity and network bandwidth
@Data
@ToString(exclude = {"loadGenerator", "networkTrafficGenerator", "connectedVMs"})
@EqualsAndHashCode(exclude = {"loadGenerator", "networkTrafficGenerator", "connectedVMs"})
public abstract class VM implements SimulationEntity {

    @NonNull
    public final int id;

    /**
     * The number of instructions available per second (=clock time * number of cores).
     */
    @NonNull
    public final int vCPUs;
    private int networkTrafficToWorld;

    public int MAX_CPU() {
        return this.vCPUs * 100;
    }

    /**
     * The amount of RAM, in GiB.
     */
    @NonNull
    private int maxRAM;
    /**
     * The maximum bandwidth of the internet connection, in Mbps.
     */
    @NonNull
    private int maxBandwidth;

    /**
     * The LoadGenerator used for the generation of load on this VM.
     */
    private LoadGenerator loadGenerator;

    /**
     * The NetworkTrafficGenerator used for the generation of network traffic.
     */
    private NetworkTrafficGenerator networkTrafficGenerator;

    /**
     * A mapping from VMs this VM is connected with to the upload speed to that VM.
     */
    private Map<VM, Integer> connectedVMs;

    /**
     * @invariant 0 <= CPU <= MAX_CPU()
     **/
    public VM(int id, int vCPUs, int maxRAM, int maxBandwidth) {
        this.id = id;
        this.vCPUs = vCPUs;
        this.maxRAM = maxRAM;
        this.maxBandwidth = maxBandwidth;
        this.CPU = (int) (Params.INITIAL_VM_CPU_USAGE * this.MAX_CPU());
        this.loadGenerator = new NormalLoadGenerator();
        this.networkTrafficGenerator = new SimpleNetworkTrafficGenerator();
        this.connectedVMs = new HashMap<VM, Integer>();
    }

    private int CPU;

    /**
     * Fluctuate the load on this VM.
     */
    private void fluctuateLoad() {
        this.CPU = (int) loadGenerator.generate(this.CPU, 0, this.MAX_CPU());
    }

    /**
     * Fluctuate the network traffic on this VM, both to the world and to connected VMs.
     */
    private void fluctuateNetworkTraffic() {
        for(Map.Entry<VM, Integer> entry : connectedVMs.entrySet()){
            entry.setValue(this.networkTrafficGenerator.generateBetweenVM(entry.getValue(), this.getMaxBandwidth()));
        }
        this.networkTrafficToWorld = this.networkTrafficGenerator.generateToWorld(this.networkTrafficToWorld, this.getMaxBandwidth());
    }

    /**
     * Connect this VM to another VM.
     * @param other The VM to connect to.
     */
    public void connectToVM(VM other) {
        this.connectedVMs.put(other, 0);
    }

    public void tick() {
        this.fluctuateLoad();
        this.fluctuateNetworkTraffic();
        System.out.println("Tick " + this.toString());
        //TODO
    }


    public enum State {
        RUNNING,
        MIGRATING,
        RESERVED
    }
}
