package simulation;

/**
 * Simple network generator which generates a fixed network traffic.
 */
public class SimpleNetworkTrafficGenerator implements NetworkTrafficGenerator {
    public int generate(int previous, int max) {
        return 10;
    }
}
