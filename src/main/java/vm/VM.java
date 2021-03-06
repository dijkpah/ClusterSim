package vm;

import cluster.Server;
import generators.LoadGenerator;
import generators.NetworkTrafficGenerator;
import generators.NormalLoadGenerator;
import generators.SimpleNetworkTrafficGenerator;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import simulation.Params;
import simulation.SimulationEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

// Each VM type is characterized by processing performance defined in MIPS, RAM
// capacity, storage capacity and network bandwidth
@Data
@ToString(exclude = {"loadGenerator", "networkTrafficGenerator", "connectedVMs", "server"})
@EqualsAndHashCode(exclude = {"loadGenerator", "networkTrafficGenerator", "connectedVMs", "server", "group"})
public abstract class VM implements SimulationEntity, Comparable{
    private final static Logger logger = Logger.getLogger(VM.class.getName());
    private VMGroup group;

    @NonNull
    public final int id;

    /**
     * The number of instructions available per second (=clock time * number of cores).
     */
    @NonNull public final int vCPUs;

    /**
     * The number of Mbs to be transferred when migrating.
     */
    @NonNull public final int size;

    private int networkTrafficToWorld;
    private Server server;


    public int MAX_CPU() {
        return this.vCPUs * 100;
    }

    /**
     * The current CPU load of this VM. Can be lower than the required CPU load as a result of an overloaded server
     */
    public double CPULoad;
    /**
     * The required CPU load.
     */
    private double requiredCPULoad;

    /**
     * The amount of RAM, in GiB.
     */
    @NonNull private int maxRAM;
    /**
     * The maximum bandwidth of the internet connection, in Mbps.
     */
    @NonNull private int maxBandwidth;

    @NonNull private State state;

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
    public VM(int id, int vCPUs, int maxRAM, int maxBandwidth, int size) {
        this.id = id;
        this.vCPUs = vCPUs;
        this.maxRAM = maxRAM;
        this.maxBandwidth = maxBandwidth;
        this.size = size;
        this.CPULoad = Params.INITIAL_VM_CPU_USAGE;
        this.state = State.RUNNING;
        this.loadGenerator = new NormalLoadGenerator();
        this.networkTrafficGenerator = new SimpleNetworkTrafficGenerator();
        this.connectedVMs = new HashMap<VM, Integer>();
    }

    public int CPU(){
        return (int) (this.MAX_CPU() * this.CPULoad);
    }

    public int getCPU(){
        return CPU();
    }

    /**
     * Fluctuate the load on this VM.
     */
    private void fluctuateLoad() {
        this.requiredCPULoad = loadGenerator.generate(this.requiredCPULoad, 0, Params.CPU_LOAD_POSSIBLE_MAX);
        this.CPULoad = this.requiredCPULoad;
    }

    /**
     * Fluctuate the network traffic on this VM, both to the world and to connected VMs.
     */
    public void fluctuateNetworkTraffic() {
        this.networkTrafficToWorld = this.networkTrafficGenerator.generateToWorld(this);
        for(Map.Entry<VM, Integer> entry : connectedVMs.entrySet()){
            entry.setValue(this.networkTrafficGenerator.generateBetweenVM(entry.getValue(), this.getMaxBandwidth() - this.networkTrafficToWorld));
        }

    }

    public void tick() {
        this.fluctuateLoad();
        this.fluctuateNetworkTraffic();
        logger.finest("Tick " + this.toString());
        //TODO
    }

    public void reset(){

    }

    public void setNetworkTrafficToVM(VM other, int networkTraffic) {
        connectedVMs.put(other, networkTraffic);
    }

    public enum State {
        RUNNING,
        MIGRATING
    }

    @Override
    public int compareTo(Object o) {
        return this.getId()-((VM)o).getId();
    }
}
