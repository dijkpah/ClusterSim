package simulation;

/**
 * Simple network generator which generates a fixed network traffic.
 */
public class SimpleNetworkTrafficGenerator implements NetworkTrafficGenerator {
    public int generateBetweenVM(int previous, int max) {
        return 5;
    }

    public int generateToWorld(int previous, int max) {
        return 20;
    }
}
