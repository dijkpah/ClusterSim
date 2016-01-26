package switches;

import cluster.Cable;
import graph.Edge;
import graph.Node;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import simulation.SimulationEntity;

import java.util.logging.Logger;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class Switch extends Node implements SimulationEntity {
    private final static Logger logger = Logger.getLogger(Switch.class.getName());

    @Getter public final int CAPACITY = 0;
    @Getter public final int BASEPOWER = 0;
    @Getter public final int MAXPOWER = 0;

    public Switch(int id){
        super(id);
    }

    @Override
    public void tick() {

    }

    @Override
    public void reset() {

    }

    public int getMaxConsumption(){
        return this.getMAXPOWER();
    }

    public int getBaseConsumption(){
        return this.getBASEPOWER();
    }

    public int getExternalCommunicationConsumption(){
        int cableBandwidthUsed = 0;
        int cableCapacity = 0;
        for(Edge edge: getEdges()){
            if(edge instanceof Cable){
                Cable cable = (Cable) edge;
                cableBandwidthUsed += cable.getExternalCommunicationBandwidth();
                cableCapacity += cable.getCapacity();
            }
        }
        logger.finest(this + ": " + cableBandwidthUsed + "/" + cableCapacity);
        return (int)((double)cableBandwidthUsed/cableCapacity * (getMaxConsumption() - getBaseConsumption()));
    }

    public int getInternalCommunicationConsumption() {
        int cableBandwidthUsed = 0;
        int cableCapacity = 0;
        for(Edge edge: getEdges()){
            if(edge instanceof Cable){
                Cable cable = (Cable) edge;
                cableBandwidthUsed += cable.getInternalCommunicationBandwidth();
                cableCapacity += cable.getCapacity();
            }
        }
        return (int)((double)cableBandwidthUsed/cableCapacity * (getMaxConsumption() - getBaseConsumption()));
    }

    public int getMigrationCommunicationConsumption() {
        int cableBandwidthUsed = 0;
        int cableCapacity = 0;
        for(Edge edge: getEdges()){
            if(edge instanceof Cable){
                Cable cable = (Cable) edge;
                cableBandwidthUsed += cable.getMigrationBandwidth();
                cableCapacity += cable.getCapacity();
            }
        }
        return (int)((double)cableBandwidthUsed/cableCapacity * (getMaxConsumption() - getBaseConsumption()));
    }

    @Override
    public String toString() {
        return "Switch(id=" + id + ")";
    }
}
