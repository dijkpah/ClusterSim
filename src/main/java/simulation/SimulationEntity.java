package simulation;

public interface SimulationEntity {

    /**
     * Is executed every tick, should be propagated to children
     */
    public void tick();

}
