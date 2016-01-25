package generators;

/**
 * Interface for generators of load of VMs.
 */
public interface LoadGenerator {
    public double generate(double previous, double min, double max);
}
