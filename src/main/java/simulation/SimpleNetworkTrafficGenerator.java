package simulation;

/**
 * Simple network generator which generates a fixed network traffic.
 */
public class SimpleNetworkTrafficGenerator implements LoadGenerator {
    public double generate(double previous, double min, double max) {
        return 10;
    }
}
