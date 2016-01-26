package cluster;

import graph.Node;
import lombok.Data;
import lombok.EqualsAndHashCode;
import vm.VM;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = true)
public class Server extends Node {
    private final static Logger logger = Logger.getLogger(Server.class.getName());
    private boolean overloaded;

    public Set<Integer> groupIds(){
        Set<Integer> result = new TreeSet<>();
        for(VM vm: vms){
            result.add(vm.getGroupId());
        }
        return result;
    }

    /**
     * Based on:
     *
     * FIVE WAYS TO REDUCE DATA CENTER SERVER POWER CONSUMPTION
     * Blackburn 2008
     */
    public static final int MAX_POWER = 300;
    public static final int MIN_POWER = 200;
    /**
     * Amount of hyperthreads
     */
    //Intel Xeon E5-2676 v3 has 12 cores and 24 hyperthreads
    //Source: http://www.cpu-world.com/CPUs/Xeon/Intel-Xeon%20E5-2676%20v3.html
    public static final int vCPUs = 12;

    /**
     * A list containing the VMs which are currently running on this server.
     */
    private List<VM> vms;
    /**
     * A list containing the VMs which are currently migrated to this server.
     */
    private List<VM> reservedVMs;

    private State state = State.AVAILABLE;

    /**
     * Maximum possible CPU load in percent (100% per core * cores)
     */
    public static int MAX_CPU = 100 * vCPUs;


    public double getPowerUsage() {
        switch (getState()) {
            case AVAILABLE:
                return MIN_POWER + ((double) this.getCPU() / MAX_CPU) * (MAX_POWER - MIN_POWER);
            case FALLING_ASLEEP:
            case WAKING_UP:
                return MAX_POWER;
            case SLEEPING:
            default:
                return 0;
        }
    }

    public Server(int id) {
        super(id);//superidee!
        this.vms = new ArrayList<>();
        this.reservedVMs = new ArrayList<>();
    }

    /**
     * Add a VM to this server.
     *
     * @param vm
     */
    public void addVM(VM vm) {
        this.vms.add(vm);
        vm.setServer(this);
    }

    /**
     * Remove a VM from this server.
     *
     * @param vm
     */
    public void removeVM(VM vm) {
        this.vms.remove(vm);
    }

    /**
     * Total used cpu
     *
     * @invariant 0<= \result <= MAX_CPU
     */
    public int getCPU() {
        int total = 0;
        for (VM vm : vms) {
            total += vm.getCPU();
        }
        return total;
    }

    /**
     * Get the assigned amount of CPU to VMs.
     *
     * @return
     */
    public int getAssignedCPU() {
        int total = 0;
        for (VM vm : vms) {
            total += vm.MAX_CPU();
        }
        return total;
    }

    /**
     * Get the amount of reserved CPU
     * @return
     */
    public int getReservedCPU() {
        int total = 0;
        for (VM vm : reservedVMs) {
            total += vm.MAX_CPU();
        }
        return total;
    }

    /**
     * Get the cpu usage caused by vms which are not migrating or reserved VMs
     * @return
     */
    public int getNonMigratingCPU() {
        int total = 0;
        for (VM vm : getNonMigratingVMs()) {
            total += vm.getCPU();
        }
        return total;
    }

    @Override
    public String toString() {
        return "Server(id=" + id + ", overloaded=" + isOverloaded() + ", cpu=" + getCPU() + ", state=" + getState() + ", assigned=" + getAssignedCPU() + ", max_cpu=" + MAX_CPU + ", #runningVMs=" + getVms().size() + ", #reservedVMs=" + getReservedVMs().size() + ")";
    }

    @Override
    public void tick() {
        //TODO: finish previously started migrations
        this.setOverloaded(false);

        //First fluctuate load of VMs
        for (VM vm : vms) {
            vm.tick();
        }

        // Cap the VM load if it exceeds the Server load
        if(this.getCPU() > this.MAX_CPU){
            this.setOverloaded(true);
            double ratio = this.MAX_CPU / (double)this.getCPU();
            for(VM vm : vms){
                vm.setCPULoad(vm.getCPULoad() * ratio);
                // Recalculate network traffic based on new load
                vm.fluctuateNetworkTraffic();
            }
        }

        logger.finest("Tick " + this.toString());

        //TODO: tag new VMs for migrations
        //TODO: update machine state if going to sleep or woken up
        //TODO: reserve room for VMs on physical machines
    }

    @Override
    public void reset() {
        for (VM vm : vms) {
            vm.reset();
        }
    }

    public boolean hasSLAViolation() {
        return getCPU() >= MAX_CPU;
    }

    /**
     * Get all VMs which are not migrating.
     */
    public List<VM> getNonMigratingVMs() {
        return vms.stream().filter(vm -> vm.getState().equals(VM.State.RUNNING)).collect(Collectors.toList());
    }

    public void addReservedVM(VM vm) {
        this.reservedVMs.add(vm);
    }

    public void removeReservedVM(VM vm) {
        this.reservedVMs.remove(vm);
    }

    public void fallAsleep() {
        this.setState(State.FALLING_ASLEEP);
    }

    public enum State {
        SLEEPING,
        FALLING_ASLEEP,
        WAKING_UP,
        AVAILABLE
    }

}
