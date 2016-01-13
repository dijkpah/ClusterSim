package simulation;

import org.apache.commons.math3.distribution.NormalDistribution;

public class NormalLoadGenerator implements LoadGenerator {

    private NormalDistribution distribution;

    public NormalLoadGenerator(){
        distribution = new NormalDistribution(0, Params.FLUCTUATION_DEVIATION);
    }

    /**
     *
     * @param previous param of previous value, with value between 0.0 and 1.0
     * @return new value, also between 0.0 and 1.0
     */
    public double generate(double previous) {
        double sample = distribution.sample();
        return Math.max(0, Math.min(1, previous + sample));
    }

    /**
     *
     * @param previous previous value from which we want to deviate
     * @param min minimal value
     * @param max maximal value
     * @return new value between min and max with a small deviation from previous
     */
    public double generate(double previous, double min, double max) {
        double sample = distribution.sample();                               //sample with mean 0 and deviation Params.FLUCTUATION_DEVIATION
        double diff = (sample * (max - min) + min);                          //we need to 'spread out' the sample over our range
        double truncated = Math.max(min, Math.min(max, previous+diff));      //now we just need to make sure the value does not go out of bounds
        return truncated;
    }
}
