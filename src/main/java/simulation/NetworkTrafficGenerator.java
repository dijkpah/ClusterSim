package simulation;

/**
 * Interface for generators of network traffic of VMs.
 */
public interface NetworkTrafficGenerator {
    public int generateBetweenVM(int previous, int max);

    public int generateToWorld(int previous, int max);
}
