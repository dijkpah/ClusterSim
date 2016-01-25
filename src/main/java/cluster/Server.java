package cluster;

import graph.Node;
import lombok.Data;
import lombok.EqualsAndHashCode;
import vm.VM;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = true)
public class Server extends Node {
    private final static Logger logger = Logger.getLogger(Server.class.getName());

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
    private List<VM> vms;

    private State state = State.AVAILABLE;

    /**
     * Maximum possible CPU load in percent (100% per core * cores)
     */
    public static int MAX_CPU = 100 * vCPUs;

    public double getPowerUsage() {
        return MIN_POWER + ((double) this.getCPU() / MAX_CPU) * (MAX_POWER - MIN_POWER);
    }

    public Server(int id) {
        super(id);//superidee!
        this.vms = new ArrayList<VM>();
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
     * Get the cpu usage caused by vms which are not migrating or reserved VMs
     * @return
     */
    public int getRunningCPU() {
        int total = 0;
        for (VM vm : getRunningVMs()) {
            total += vm.getCPU();
        }
        return total;
    }

    @Override
    public String toString() {
        return "Server(id=" + id + ", cpu=" + getCPU() + ", assigned=" + getAssignedCPU() + ", max_cpu=" + MAX_CPU + ")";
    }

    @Override
    public void tick() {
        //TODO: finish previously started migrations

        //First fluctuate load of VMs
        for (VM vm : vms) {
            vm.tick();
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
     * Get all VMs which are not migrating and not reserved space.
     */
    public List<VM> getRunningVMs() {
        return vms.stream().filter(vm -> vm.getState().equals(VM.State.RUNNING)).collect(Collectors.toList());
    }

    public enum State {
        SLEEPING,
        AVAILABLE
    }

}
