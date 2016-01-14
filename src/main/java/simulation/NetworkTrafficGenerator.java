package simulation;

/**
 * Interface for generators of network traffic of VMs.
 */
public interface NetworkTrafficGenerator {
    public int generate(int previous, int max);
}
