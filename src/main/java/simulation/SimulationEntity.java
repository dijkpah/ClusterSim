package simulation;

public interface SimulationEntity {

    /**
     * Is executed every tick, should be propagated to children
     */
    public void tick();

    /**
     * Reset the simulation for a new tick.
     */
    public void reset();

}
