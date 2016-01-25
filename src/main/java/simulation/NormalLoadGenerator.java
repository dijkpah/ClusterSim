package simulation;

import org.apache.commons.math3.distribution.NormalDistribution;

public class NormalLoadGenerator implements LoadGenerator {

    private NormalDistribution distribution;

    public NormalLoadGenerator(){
        distribution = new NormalDistribution(0, Params.CPU_LOAD_FLUCTUATION_DEVIATION);
    }

    /**
     *
     * @param previous previous value from which we want to deviate
     * @param min minimal value
     * @param max maximal value
     * @return new value between min and max with a small deviation from previous
     */
    public double generate(double previous, double min, double max) {
        double diff = distribution.sample();                               //sample with mean 0 and deviation Params.CPU_LOAD_FLUCTUATION_DEVIATION
        //int diff = (int) (sample * (max - min) + min);                          //we need to 'spread out' the sample over our range
        return Math.max(min, Math.min(max, previous+diff));      //now we just need to make sure the value does not go out of bounds
    }
}
