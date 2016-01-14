package switches;

import cluster.Cable;
import graph.Edge;
import graph.Node;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import simulation.SimulationEntity;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class Switch extends Node implements SimulationEntity {

    @Getter public final int CAPACITY = 0;
    @Getter public final int BASEPOWER = 0;
    @Getter public final int MAXPOWER = 0;

    public Switch(int id){
        super(id);
    }

    @Override
    public void tick() {

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
                cableBandwidthUsed += ((Cable) edge).getExternalCommunicationBandwidth();
                cableCapacity += ((Cable) edge).getCapacity();

            }
        }
        System.out.println(this + ": " + cableBandwidthUsed + "/" + cableCapacity);
        return (int)((double)cableBandwidthUsed/cableCapacity * (getMaxConsumption() - getBaseConsumption()));
    }

    public int getInternalCommunicationConsumption() {
        int cableBandwidthUsed = 0;
        int cableCapacity = 0;
        for(Edge edge: getEdges()){
            if(edge instanceof Cable){
                cableBandwidthUsed += ((Cable) edge).getInternalCommunicationBandwidth();
                cableCapacity += ((Cable) edge).getCapacity();
            }
        }
        return (int)((double)cableBandwidthUsed/cableCapacity * (getMaxConsumption() - getBaseConsumption()));
    }

    public int getMigrationCommunicationConsumption() {
        int cableBandwidthUsed = 0;
        int cableCapacity = 0;
        for(Edge edge: getEdges()){
            if(edge instanceof Cable){
                cableBandwidthUsed += ((Cable) edge).getMigrationBandwidth();
                cableCapacity += ((Cable) edge).getCapacity();
            }
        }
        return (int)((double)cableBandwidthUsed/cableCapacity * (getMaxConsumption() - getBaseConsumption()));
    }

    @Override
    public String toString() {
        return "Switch(id=" + id + ")";
    }

}
