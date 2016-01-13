package simulation;

public interface LoadGenerator {
    public double generate(double previous);
    public double generate(double previous, double min, double max);
}
