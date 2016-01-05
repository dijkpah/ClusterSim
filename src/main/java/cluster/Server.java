package cluster;

import graph.Node;
import lombok.Data;
import vm.VM;

import java.util.ArrayList;
import java.util.List;

@Data
public class Server extends Node {

    /**
     * Based on:
     *
     * FIVE WAYS TO REDUCE DATA CENTER SERVER POWER CONSUMPTION
     * Blackburn 2008
     */
    public static final int MAX_POWER = 300;
    public static final int MIN_POWER = 200;
    private List<VM> vms;

    /**Maximum possible CPU load in MIPS*/
    public static int MAX_CPU = 0;

    public double getPowerUsage(){
        return MIN_POWER + (this.getCPU()/MAX_CPU) * (MAX_POWER - MIN_POWER);
    }

    public Server(int MAX_CPU){
        this.MAX_CPU = MAX_CPU;
        this.vms = new ArrayList<VM>();
    }

    /**
     * Add a VM to this server.
     * @param vm
     */
    public void addVM(VM vm){
        this.vms.add(vm);
    }

    /**
     * Remove a VM from this server.
     * @param vm
     */
    public void removeVM(VM vm){
        this.vms.remove(vm);
    }

    /**
     * Total MIPS done now
     * @invariant 0<= \result <= MAX_CPU
     */
    public int getCPU(){
        int total = 0;
        for(VM vm : vms){
            total += vm.getCPU();
        }
        return total;
    }

    /**
     * Get the assigned amount of CPU to VMs.
     * @return
     */
    public int getAssignedCPU(){
        int total = 0;
        for(VM vm : vms){
            total += vm.getMaxCPU();
        }
        return total;
    }

    @Override
    public void tick() {
        for(VM vm : vms){
            vm.tick();
        }
        //TODO
    }
}
