package simulation;

import org.apache.commons.math3.distribution.NormalDistribution;

public class NormalLoadGenerator implements LoadGenerator {

    private NormalDistribution distribution;

    public NormalLoadGenerator(){
        distribution = new NormalDistribution(0, 0.20);
    }

    public double generate(double previous) {
       return Math.max(0.0, Math.min(1.0, previous + distribution.sample()));
    }
}
