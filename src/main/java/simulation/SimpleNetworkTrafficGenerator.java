package simulation;

/**
 * Simple network generator which generates a fixed network traffic.
 */
public class SimpleNetworkTrafficGenerator implements NetworkTrafficGenerator {
    public int generateBetweenVM(int previous, int max) {
        return Params.NETWORK_USAGE_VM_TO_VM_AVERAGE;
    }

    public int generateToWorld(int previous, int max) {
        return Params.NETWORK_USAGE_VM_TO_WORLD_AVERAGE;
    }
}
