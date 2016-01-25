package generators;

import generators.NetworkTrafficGenerator;
import simulation.Params;
import vm.VM;

/**
 * Simple network generator which generates a fixed network traffic.
 */
public class SimpleNetworkTrafficGenerator implements NetworkTrafficGenerator {
    public int generateBetweenVM(int previous, int max) {
        return Params.NETWORK_USAGE_VM_TO_VM_AVERAGE;
    }

    public int generateToWorld(VM vm) {
        return (int) (vm.getCPULoad() * vm.getMaxBandwidth() * Params.NETWORK_USAGE_VM_TO_WORLD_PERCENTAGE);
    }
}
